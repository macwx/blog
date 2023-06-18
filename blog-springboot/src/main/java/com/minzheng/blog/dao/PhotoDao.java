package com.minzheng.blog.dao;

import com.minzheng.blog.entity.Photo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;


/**
 * 照片映射器
 *
 * @author yezhiqiu
 * @date 2021/08/04
 */
@Repository
public interface PhotoDao extends BaseMapper<Photo> {

    /**
     * 随机获取相册中一条数据
     * @param albumId
     * @return
     */
    String randomPhoto(Integer albumId);



}




