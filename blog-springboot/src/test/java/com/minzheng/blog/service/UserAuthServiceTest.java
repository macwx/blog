package com.minzheng.blog.service;

import com.minzheng.blog.BlogApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;


@SpringBootTest(classes = BlogApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class UserAuthServiceTest {

    @Resource
    private UserAuthService userAuthService;

    @Test
    public void testSms(){
        userAuthService.sendCode("1055215129@qq.com");
    }

}
