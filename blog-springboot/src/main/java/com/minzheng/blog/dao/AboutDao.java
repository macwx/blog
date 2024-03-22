package com.minzheng.blog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.minzheng.blog.dto.*;
import com.minzheng.blog.entity.About;
import com.minzheng.blog.entity.Article;
import com.minzheng.blog.vo.ConditionVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 文章
 *
 * @author yezhiqiu
 * @date 2021/08/10
 */
@Repository
public interface AboutDao extends BaseMapper<About> {

}
