package com.minzheng.blog.service.impl;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.minzheng.blog.dao.ArticleDao;
import com.minzheng.blog.dao.ArticleTagDao;
import com.minzheng.blog.dao.CategoryDao;
import com.minzheng.blog.dao.TagDao;
import com.minzheng.blog.dto.*;
import com.minzheng.blog.entity.Article;
import com.minzheng.blog.entity.ArticleTag;
import com.minzheng.blog.entity.Category;
import com.minzheng.blog.entity.Tag;
import com.minzheng.blog.enums.FileExtEnum;
import com.minzheng.blog.enums.FilePathEnum;
import com.minzheng.blog.enums.YesOrNoEnum;
import com.minzheng.blog.exception.BizException;
import com.minzheng.blog.service.*;
import com.minzheng.blog.strategy.context.SearchStrategyContext;
import com.minzheng.blog.strategy.context.UploadStrategyContext;
import com.minzheng.blog.util.BeanCopyUtils;
import com.minzheng.blog.util.CommonUtils;
import com.minzheng.blog.util.PageUtils;
import com.minzheng.blog.util.UserUtils;
import com.minzheng.blog.vo.*;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.util.data.MutableDataSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.minzheng.blog.common.ResultCode.*;
import static com.minzheng.blog.constant.CommonConst.*;
import static com.minzheng.blog.constant.RedisPrefixConst.*;
import static com.minzheng.blog.enums.ArticleStatusEnum.DRAFT;
import static com.minzheng.blog.enums.ArticleStatusEnum.PUBLIC;


/**
 * ????????????
 *
 * @author yezhiqiu
 * @date 2021/08/10
 */
@Service
@Slf4j
public class ArticleServiceImpl extends ServiceImpl<ArticleDao, Article> implements ArticleService {
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private TagDao tagDao;
    @Autowired
    private TagService tagService;
    @Autowired
    private ArticleTagDao articleTagDao;
    @Autowired
    private SearchStrategyContext searchStrategyContext;
    @Autowired
    private HttpSession session;
    @Autowired
    private RedisService redisService;
    @Autowired
    private ArticleTagService articleTagService;
    @Autowired
    private BlogInfoService blogInfoService;
    @Autowired
    private UploadStrategyContext uploadStrategyContext;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${baidu.url}")
    private String baiduUrl;

    @Override
    public PageResult<ArchiveDTO> listArchives() {
        Page<Article> page = new Page<>(PageUtils.getCurrent(), PageUtils.getSize());
        // ??????????????????
        Page<Article> articlePage = articleDao.selectPage(page, new LambdaQueryWrapper<Article>()
                .select(Article::getId, Article::getArticleTitle, Article::getCreateTime).orderByDesc(Article::getCreateTime)
                .eq(Article::getIsDelete, FALSE)
                .eq(Article::getStatus, PUBLIC.getStatus()));
        List<ArchiveDTO> archiveDTOList = BeanCopyUtils.copyList(articlePage.getRecords(), ArchiveDTO.class);
        return new PageResult<>(archiveDTOList, (int) articlePage.getTotal());
    }

    @Override
    public PageResult<ArticleBackDTO> listArticleBacks(ConditionVO condition) {
        // ??????????????????
        Integer count = articleDao.countArticleBacks(condition);
        if (count == 0) {
            return new PageResult<>();
        }
        // ??????????????????
        List<ArticleBackDTO> articleBackDTOList = articleDao.listArticleBacks(PageUtils.getLimitCurrent(), PageUtils.getSize(), condition);
        // ?????????????????????????????????
        Map<Object, Double> viewsCountMap = redisService.zAllScore(ARTICLE_VIEWS_COUNT);
        Map<String, Object> likeCountMap = redisService.hGetAll(ARTICLE_LIKE_COUNT);
        // ???????????????????????????
        articleBackDTOList.forEach(item -> {
            Double viewsCount = viewsCountMap.get(item.getId());
            if (Objects.nonNull(viewsCount)) {
                item.setViewsCount(viewsCount.intValue());
            }
            item.setLikeCount((Integer) likeCountMap.get(item.getId().toString()));
        });
        return new PageResult<>(articleBackDTOList, count);
    }

    @Override
    public List<ArticleHomeDTO> listArticles() {
        return articleDao.listArticles(PageUtils.getLimitCurrent(), PageUtils.getSize());
    }

    @Override
    public ArticlePreviewListDTO listArticlesByCondition(ConditionVO condition) {
        // ????????????
        List<ArticlePreviewDTO> articlePreviewDTOList = articleDao.listArticlesByCondition(PageUtils.getLimitCurrent(), PageUtils.getSize(), condition);
        // ?????????????????????(??????????????????)
        String name;
        if (Objects.nonNull(condition.getCategoryId())) {
            name = categoryDao.selectOne(new LambdaQueryWrapper<Category>().select(Category::getCategoryName)
                    .eq(Category::getId, condition.getCategoryId())).getCategoryName();
        } else {
            name = tagService.getOne(new LambdaQueryWrapper<Tag>()
                    .select(Tag::getTagName).eq(Tag::getId, condition.getTagId())).getTagName();
        }
        return ArticlePreviewListDTO.builder().articlePreviewDTOList(articlePreviewDTOList).name(name).build();
    }

    @Override
    public ArticleDTO getArticleById(Integer articleId) {
        // ??????????????????
        CompletableFuture<List<ArticleRecommendDTO>> recommendArticleList = CompletableFuture.supplyAsync(() -> articleDao.listRecommendArticles(articleId));
        // ??????????????????
        CompletableFuture<List<ArticleRecommendDTO>> newestArticleList = CompletableFuture.supplyAsync(() -> {
            List<Article> articleList = articleDao.selectList(new LambdaQueryWrapper<Article>()
                    .select(Article::getId, Article::getArticleTitle, Article::getArticleCover, Article::getCreateTime).eq(Article::getIsDelete, FALSE)
                    .eq(Article::getStatus, PUBLIC.getStatus()).orderByDesc(Article::getId).last("limit 5"));
            return BeanCopyUtils.copyList(articleList, ArticleRecommendDTO.class);
        });
        // ??????id????????????
        ArticleDTO article = articleDao.getArticleById(articleId);
        if (Objects.isNull(article)) {
            throw new BizException("???????????????");
        }
        if (Objects.nonNull(article.getFileAttach())) {
            List<FileVo> fileVoList = JSONArray.parseArray(article.getFileAttach(), FileVo.class);
            article.setFileVoAttachList(fileVoList);
        }
        // ?????????????????????
        updateArticleViewsCount(articleId);
        // ??????????????????????????????
        Article lastArticle = articleDao.selectOne(new LambdaQueryWrapper<Article>()
                .select(Article::getId, Article::getArticleTitle, Article::getArticleCover).eq(Article::getIsDelete, FALSE)
                .eq(Article::getStatus, PUBLIC.getStatus())
                .lt(Article::getId, articleId)
                .orderByDesc(Article::getId).last("limit 1"));
        Article nextArticle = articleDao.selectOne(new LambdaQueryWrapper<Article>()
                .select(Article::getId, Article::getArticleTitle, Article::getArticleCover).eq(Article::getIsDelete, FALSE)
                .eq(Article::getStatus, PUBLIC.getStatus())
                .gt(Article::getId, articleId).orderByAsc(Article::getId)
                .last("limit 1"));
        article.setLastArticle(BeanCopyUtils.copyObject(lastArticle, ArticlePaginationDTO.class));
        article.setNextArticle(BeanCopyUtils.copyObject(nextArticle, ArticlePaginationDTO.class));
        // ???????????????????????????
        Double score = redisService.zScore(ARTICLE_VIEWS_COUNT, articleId);
        if (Objects.nonNull(score)) {
            article.setViewsCount(score.intValue());
        }
        article.setLikeCount((Integer) redisService.hGet(ARTICLE_LIKE_COUNT, articleId.toString()));
        // ??????????????????
        try {
            article.setRecommendArticleList(recommendArticleList.get());
            article.setNewestArticleList(newestArticleList.get());
        } catch (Exception e) {
            log.error(StrUtil.format("????????????:{}", ExceptionUtil.stacktraceToString(e)));
        }
        return article;
    }


    @Override
    public void saveArticleLike(Integer articleId, HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        System.out.println(sessionId);
        // ??????????????????
        String articleLikeKey = ARTICLE_USER_LIKE +sessionId;
        if (redisService.sIsMember(articleLikeKey, articleId)) {
            // ????????????????????????id
            redisService.sRemove(articleLikeKey, articleId);
            // ???????????????-1
            redisService.hDecr(ARTICLE_LIKE_COUNT, articleId.toString(), 1L);
        } else {
            // ????????????????????????id
            redisService.sAdd(articleLikeKey, articleId);
            // ???????????????+1
            redisService.hIncr(ARTICLE_LIKE_COUNT, articleId.toString(), 1L);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrUpdateArticle(ArticleVO articleVO) {
        // ????????????????????????
        CompletableFuture<WebsiteConfigVO> webConfig = CompletableFuture.supplyAsync(() -> blogInfoService.getWebsiteConfig());

        // ??????????????????
        Category category = saveArticleCategory(articleVO);
        // ?????????????????????
        Article article = BeanCopyUtils.copyObject(articleVO, Article.class);
        if (Objects.nonNull(category)) {
            article.setCategoryId(category.getId());
        }
        if (Objects.nonNull(articleVO.getFileAttach())){
            List<FileVo> fileVoList = articleVO.getFileAttach();
            article.setFileAttach( JSONArray.toJSONString(fileVoList));
        }
        // ????????????????????????
        if (StrUtil.isBlank(article.getArticleCover())){
            try {
                article.setArticleCover(webConfig.get().getArticleCover()+"&time="+CommonUtils.getRandomCode());
            } catch (Exception e) {
                throw new BizException("??????????????????????????????");
            }
        }
        article.setUserId(UserUtils.getLoginUser().getUserInfoId());
        this.saveOrUpdate(article);
        // ??????????????????
        saveArticleTag(articleVO, article.getId());
    }

    /**
     * ??????????????????
     *
     * @param articleVO ????????????
     * @return {@link Category} ????????????
     */
    private Category saveArticleCategory(ArticleVO articleVO) {
        // ????????????????????????
        Category category = categoryDao.selectOne(new LambdaQueryWrapper<Category>().eq(Category::getCategoryName, articleVO.getCategoryName()));
        if (Objects.isNull(category) && !articleVO.getStatus().equals(DRAFT.getStatus())) {
            category = Category.builder().categoryName(articleVO.getCategoryName()).build();
            categoryDao.insert(category);
        }
        return category;
    }

    @Override
    public void updateArticleTop(ArticleTopVO articleTopVO) {
        // ????????????????????????
        Article article = Article.builder().id(articleTopVO.getId()).isTop(articleTopVO.getIsTop()).build();
        articleDao.updateById(article);
    }

    @Override
    public void updateArticleDelete(DeleteVO deleteVO) {
        // ??????????????????????????????
        List<Article> articleList = deleteVO.getIdList().stream().map(id -> Article.builder()
                        .id(id)
                        .isTop(FALSE)
                        .isDelete(deleteVO.getIsDelete())
                        .build())
                .collect(Collectors.toList());
        this.updateBatchById(articleList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteArticles(List<Integer> articleIdList) {
        // ????????????????????????
        articleTagDao.delete(new LambdaQueryWrapper<ArticleTag>().in(ArticleTag::getArticleId, articleIdList));
        // ????????????
        articleDao.deleteBatchIds(articleIdList);
    }

    @Override
    public List<String> exportArticles(List<Integer> articleIdList) {
        // ??????????????????
        List<Article> articleList = articleDao.selectList(new LambdaQueryWrapper<Article>()
                .select(Article::getArticleTitle, Article::getArticleContent)
                .in(Article::getId, articleIdList));
        // ?????????????????????
        List<String> urlList = new ArrayList<>();
        for (Article article : articleList) {
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(article.getArticleContent().getBytes())) {
                String url = uploadStrategyContext.executeUploadStrategy(article.getArticleTitle() + FileExtEnum.MD.getExtName(), inputStream, FilePathEnum.MD.getPath());
                urlList.add(url);
            } catch (Exception e) {
                log.error(StrUtil.format("??????????????????,??????:{}", ExceptionUtil.stacktraceToString(e)));
                throw new BizException("??????????????????");
            }
        }
        return urlList;
    }



    @Override
    public List<ArticleSearchDTO> listArticlesBySearch(ConditionVO condition) {
        return searchStrategyContext.executeSearchStrategy(condition.getKeywords());
    }

    @Override
    public ArticleVO getArticleBackById(Integer articleId) {
        // ??????????????????
        Article article = articleDao.selectById(articleId);
        // ??????????????????
        Category category = categoryDao.selectById(article.getCategoryId());
        String categoryName = null;
        if (Objects.nonNull(category)) {
            categoryName = category.getCategoryName();
        }
        // ??????????????????
        List<String> tagNameList = tagDao.listTagNameByArticleId(articleId);
        // ????????????
        ArticleVO articleVO = BeanCopyUtils.copyObject(article, ArticleVO.class);
        if (Objects.nonNull(article.getFileAttach())){
            articleVO.setFileAttach(JSONArray.parseArray(article.getFileAttach().replace("\n", "").replace("\t", ""),FileVo.class));
        }
        articleVO.setCategoryName(categoryName);
        articleVO.setTagNameList(tagNameList);
        return articleVO;
    }


    /**
     * ?????????????????????
     *
     * @param articleId ??????id
     */
    public void updateArticleViewsCount(Integer articleId) {
        // ?????????????????????????????????????????????
        Set<Integer> articleSet = CommonUtils.castSet(Optional.ofNullable(session.getAttribute(ARTICLE_SET)).orElseGet(HashSet::new), Integer.class);
        if (!articleSet.contains(articleId)) {
            articleSet.add(articleId);
            session.setAttribute(ARTICLE_SET, articleSet);
            // ?????????+1
            redisService.zIncr(ARTICLE_VIEWS_COUNT, articleId, 1D);
        }
    }

    /**
     * ??????????????????
     *
     * @param articleVO ????????????
     */
    private void saveArticleTag(ArticleVO articleVO, Integer articleId) {
        // ???????????????????????????????????????
        if (Objects.nonNull(articleVO.getId())) {
            articleTagDao.delete(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, articleVO.getId()));
        }
        // ??????????????????
        List<String> tagNameList = articleVO.getTagNameList();
        if (CollectionUtils.isNotEmpty(tagNameList)) {
            // ????????????????????????
            List<Tag> existTagList = tagService.list(new LambdaQueryWrapper<Tag>().in(Tag::getTagName, tagNameList));
            List<String> existTagNameList = existTagList.stream().map(Tag::getTagName).collect(Collectors.toList());
            List<Integer> existTagIdList = existTagList.stream().map(Tag::getId).collect(Collectors.toList());
            // ??????????????????????????????
            tagNameList.removeAll(existTagNameList);
            if (CollectionUtils.isNotEmpty(tagNameList)) {
                List<Tag> tagList = tagNameList.stream().map(item -> Tag.builder().tagName(item).build()).collect(Collectors.toList());
                tagService.saveBatch(tagList);
                List<Integer> tagIdList = tagList.stream().map(Tag::getId).collect(Collectors.toList());
                existTagIdList.addAll(tagIdList);
            }
            // ????????????id????????????
            List<ArticleTag> articleTagList = existTagIdList.stream().map(item -> ArticleTag.builder()
                            .articleId(articleId)
                            .tagId(item)
                            .build())
                    .collect(Collectors.toList());
            articleTagService.saveBatch(articleTagList);
        }
    }


    @Override
    public void articleSeo(List<Integer> ids) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Host", "data.zz.baidu.com");
        headers.add("User-Agent", "curl/7.12.1");
        headers.add("Content-Length", "83");
        headers.add("Content-Type", "text/plain");
        ids.forEach(item -> {
            String url = "http://www.shiyit.com/article/" + item;
            HttpEntity<String> entity = new HttpEntity<>(url, headers);
            restTemplate.postForObject(baiduUrl, entity, String.class);
        });
    }

    @Override
    public Result reptile(String url) {
        try {
            Document document = Jsoup.connect(url).get();
            Elements title  = document.getElementsByClass("title-article");
            Elements tags  = document.getElementsByClass("tag-link");
            Elements content  = document.getElementsByClass("article_content");
            Assert.isTrue(StringUtils.isNotBlank(content.toString()),CRAWLING_ARTICLE_FAILED.getDesc());
            //????????????HTML?????????????????????MD???????????????
            String newContent = content.get(0).toString().replaceAll("<code>", "<code class=\"lang-java\">");
            MutableDataSet options = new MutableDataSet();
            String markdown = FlexmarkHtmlConverter.builder(options).build().convert(newContent)
                    .replace("lang-java","java");

            //?????????????????? ???https://api.btstu.cn/?????????????????????
            String strResult = restTemplate.getForObject(IMG_URL_API, String.class);
            JSONObject jsonObject = JSON.parseObject(strResult);
            Object imgUrl = jsonObject.get("imgurl");

            Article entity = Article.builder().userId(1)
                    // TODO articleContent MD
                    .articleContent(markdown)
//                    .articleContent(newContent)
                    .categoryId(OTHER_CATEGORY_ID)
                    .type(YesOrNoEnum.NO.getCode())
                    .originalUrl(url)
                    .articleTitle(title.get(0).text()).articleCover(imgUrl.toString())
                    .build();
            baseMapper.insert(entity);

            //????????????????????????
            List<Integer> tagsId = new ArrayList<>();
            tags.forEach(item ->{
                String tag = item.text();
                Tag result = tagDao.selectOne(new LambdaQueryWrapper<Tag>().eq(Tag::getTagName, tag ));
                if (result == null){
                    result = Tag.builder().tagName(tag).build();
                    tagDao.insert(result);
                }
                tagsId.add(result.getId());
            });
            articleTagDao.saveArticleTags(entity.getId(),tagsId);

            log.info("??????????????????????????????:{}", JSON.toJSONString(entity));
        } catch (IOException e) {
            log.error(StrUtil.format("????????????:{}", ExceptionUtil.stacktraceToString(e)));
        }
        return Result.ok("??????????????????");
    }

    @Override
    public Result randomImg() {
        //?????????????????? ???https://api.btstu.cn/?????????????????????
        String result = restTemplate.getForObject(IMG_URL_API, String.class);
        Object imgUrl = JSON.parseObject(result).get("imgurl");
        return Result.ok(imgUrl);
    }


}
