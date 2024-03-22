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
import com.minzheng.blog.dao.*;
import com.minzheng.blog.dto.*;
import com.minzheng.blog.entity.*;
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

import static com.minzheng.blog.common.ResultCode.CRAWLING_ARTICLE_FAILED;
import static com.minzheng.blog.constant.CommonConst.*;
import static com.minzheng.blog.constant.RedisPrefixConst.*;
import static com.minzheng.blog.enums.ArticleStatusEnum.DRAFT;
import static com.minzheng.blog.enums.ArticleStatusEnum.PUBLIC;


/**
 * 文章服务
 *
 * @author yezhiqiu
 * @date 2021/08/10
 */
@Service
@Slf4j
public class AboutServiceImpl extends ServiceImpl<AboutDao, About> implements AboutService {


}
