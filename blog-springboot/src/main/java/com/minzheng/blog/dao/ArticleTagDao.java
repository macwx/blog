package com.minzheng.blog.dao;

import com.minzheng.blog.entity.ArticleTag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 文章标签
 *
 * @author yezhiqiu
 * @date 2021/08/10
 */
@Repository
public interface ArticleTagDao extends BaseMapper<ArticleTag> {

    void saveArticleTags(@Param("articleId") Integer articleId, @Param("tags") List<Integer> tags);

}
