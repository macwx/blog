package com.minzheng.blog.controller;


import com.minzheng.blog.annotation.OptLog;
import com.minzheng.blog.dto.*;
import com.minzheng.blog.enums.FilePathEnum;
import com.minzheng.blog.service.ArticleService;
import com.minzheng.blog.strategy.context.ArticleImportStrategyContext;
import com.minzheng.blog.strategy.context.UploadStrategyContext;
import com.minzheng.blog.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.minzheng.blog.constant.OptTypeConst.*;

/**
 * 文章控制器
 *
 * @author yezhiqiu
 * @date 2021/07/28
 */
@Api(tags = "文章模块")
@RestController
public class ArticleController {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private UploadStrategyContext uploadStrategyContext;
    @Autowired
    private ArticleImportStrategyContext articleImportStrategyContext;

    /**
     * 查看文章归档
     *
     * @return {@link Result<ArchiveDTO>} 文章归档列表
     */
    @ApiOperation(value = "查看文章归档")
    @GetMapping("/articles/archives")
    public Result<PageResult<ArchiveDTO>> listArchives() {
        return Result.ok(articleService.listArchives());
    }

    /**
     * 查看首页文章
     *
     * @return {@link Result<ArticleHomeDTO>} 首页文章列表
     */
    @ApiOperation(value = "查看首页文章")
    @GetMapping("/articles")
    public Result<List<ArticleHomeDTO>> listArticles() {
        return Result.ok(articleService.listArticles());
    }

    /**
     * 查看后台文章
     *
     * @param conditionVO 条件
     * @return {@link Result<ArticleBackDTO>} 后台文章列表
     */
    @ApiOperation(value = "查看后台文章")
    @GetMapping("/admin/articles")
    public Result<PageResult<ArticleBackDTO>> listArticleBacks(ConditionVO conditionVO) {
        return Result.ok(articleService.listArticleBacks(conditionVO));
    }

    /**
     * 添加或修改文章
     *
     * @param articleVO 文章信息
     * @return {@link Result<>}
     */
    @OptLog(optType = SAVE_OR_UPDATE)
    @ApiOperation(value = "添加或修改文章")
    @PostMapping("/admin/articles")
    public Result<?> saveOrUpdateArticle(@Valid @RequestBody ArticleVO articleVO) {
        articleService.saveOrUpdateArticle(articleVO);
        return Result.ok();
    }

    /**
     * 修改文章置顶状态
     *
     * @param articleTopVO 文章置顶信息
     * @return {@link Result<>}
     */
    @OptLog(optType = UPDATE)
    @ApiOperation(value = "修改文章置顶")
    @PutMapping("/admin/articles/top")
    public Result<?> updateArticleTop(@Valid @RequestBody ArticleTopVO articleTopVO) {
        articleService.updateArticleTop(articleTopVO);
        return Result.ok();
    }

    /**
     * 恢复或删除文章
     *
     * @param deleteVO 逻辑删除信息
     * @return {@link Result<>}
     */
    @OptLog(optType = UPDATE)
    @ApiOperation(value = "恢复或删除文章")
    @PutMapping("/admin/articles")
    public Result<?> updateArticleDelete(@Valid @RequestBody DeleteVO deleteVO) {
        articleService.updateArticleDelete(deleteVO);
        return Result.ok();
    }

    /**
     * 上传文章图片
     *
     * @param file 文件
     * @return {@link Result<String>} 文章图片地址
     */
    @ApiOperation(value = "上传文章图片")
    @ApiImplicitParam(name = "file", value = "文章图片", required = true, dataType = "MultipartFile")
    @PostMapping("/admin/articles/images")
    public Result<String> saveArticleImages(MultipartFile file) {
        return Result.ok(uploadStrategyContext.executeUploadStrategy(file, FilePathEnum.ARTICLE.getPath()));
    }

    @ApiOperation(value = "上传附件")
    @ApiImplicitParam(name = "file", value = "文章附件", required = true, dataType = "MultipartFile")
    @PostMapping("/admin/articles/attach")
    public Result<FileVo> saveArticleAttach(MultipartFile file) {
        FileVo fileVo = new FileVo();
        fileVo.setName(file.getOriginalFilename());
        fileVo.setUrl(uploadStrategyContext.executeUploadStrategy(file, FilePathEnum.ARTICLE.getPath()));
        return Result.ok(fileVo);
    }

    /**
     * 删除文章
     *
     * @param articleIdList 文章id列表
     * @return {@link Result<>}
     */
    @OptLog(optType = REMOVE)
    @ApiOperation(value = "物理删除文章")
    @DeleteMapping("/admin/articles")
    public Result<?> deleteArticles(@RequestBody List<Integer> articleIdList) {
        articleService.deleteArticles(articleIdList);
        return Result.ok();
    }

    /**
     * 根据id查看后台文章
     *
     * @param articleId 文章id
     * @return {@link Result<ArticleVO>} 后台文章
     */
    @ApiOperation(value = "根据id查看后台文章")
    @ApiImplicitParam(name = "articleId", value = "文章id", required = true, dataType = "Integer")
    @GetMapping("/admin/articles/{articleId}")
    public Result<ArticleVO> getArticleBackById(@PathVariable("articleId") Integer articleId) {
        return Result.ok(articleService.getArticleBackById(articleId));
    }

    /**
     * 根据id查看文章
     *
     * @param articleId 文章id
     * @return {@link Result<ArticleDTO>} 文章信息
     */
    @ApiOperation(value = "根据id查看文章")
    @ApiImplicitParam(name = "articleId", value = "文章id", required = true, dataType = "Integer")
    @GetMapping("/articles/{articleId}")
    public Result<ArticleDTO> getArticleById(@PathVariable("articleId") Integer articleId) {
        return Result.ok(articleService.getArticleById(articleId));
    }

    /**
     * 根据条件查询文章
     *
     * @param condition 条件
     * @return {@link Result<ArticlePreviewListDTO>} 文章列表
     */
    @ApiOperation(value = "根据条件查询文章")
    @GetMapping("/articles/condition")
    public Result<ArticlePreviewListDTO> listArticlesByCondition(ConditionVO condition) {
        return Result.ok(articleService.listArticlesByCondition(condition));
    }

    /**
     * 搜索文章
     *
     * @param condition 条件
     * @return {@link Result<ArticleSearchDTO>} 文章列表
     */
    @ApiOperation(value = "搜索文章")
    @GetMapping("/articles/search")
    public Result<List<ArticleSearchDTO>> listArticlesBySearch(ConditionVO condition) {
        return Result.ok(articleService.listArticlesBySearch(condition));
    }

    /**
     * 点赞文章
     *
     * @param articleId 文章id
     * @return {@link Result<>}
     */
    @ApiOperation(value = "点赞文章")
    @ApiImplicitParam(name = "articleId", value = "文章id", required = true, dataType = "Integer")
    @PostMapping("/articles/{articleId}/like")
    public Result<?> saveArticleLike(@PathVariable("articleId") Integer articleId, HttpServletRequest request) {
        articleService.saveArticleLike(articleId,request);
        return Result.ok();
    }

    /**
     * 导出文章
     *
     * @param articleIdList 文章id列表
     * @return {@link List<String>} 文件url列表
     */
    @ApiOperation(value = "导出文章")
    @ApiImplicitParam(name = "articleIdList", value = "文章id", required = true, dataType = "List<Integer>")
    @PostMapping("/admin/articles/export")
    public Result<List<String>> exportArticles(@RequestBody List<Integer> articleIdList) {
        return Result.ok(articleService.exportArticles(articleIdList));
    }

    /**
     * 导入文章
     *
     * @param file 文件
     * @param type 文章类型
     * @return {@link Result<>}
     */
    @ApiOperation(value = "导入文章")
    @PostMapping("/admin/articles/import")
    public Result<?> importArticles(MultipartFile file, @RequestParam(required = false) String type) {
        articleImportStrategyContext.importArticles(file, type);
        return Result.ok();
    }

    @PostMapping(value = "/admin/articles/baiduSeo")
    @ApiOperation(value = "文章SEO", httpMethod = "POST",notes = "文章SEO")
    public  Result<?>  articleSeo(@RequestBody List<Integer> ids) {
        articleService.articleSeo(ids);
        return Result.ok();
    }

    @GetMapping(value = "/admin/articles/reptile")
    @ApiOperation(value = "文章爬虫", httpMethod = "GET",notes = "文章爬虫")
    public Result<?>  reptile(String url) {
        return articleService.reptile(url);
    }

    @GetMapping(value = "/randomImg")
    @ApiOperation(value = "随机获取一张图片", httpMethod = "GET", response = Result.class, notes = "随机获取一张图片")
    public Result<?>  randomImg() {
        return articleService.randomImg();
    }

}
