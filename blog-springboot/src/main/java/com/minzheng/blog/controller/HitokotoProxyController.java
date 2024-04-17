package com.minzheng.blog.controller;


import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @Author: 马超伟
 * @CreateTime: 2024-04-17
 * @Description: 代理一言打字机
 * @Version: 1.0
 */

@RestController
public class HitokotoProxyController {

    private final RestTemplate restTemplate;

    public HitokotoProxyController(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @GetMapping("/hitokoto")
    public String getHitokoto() {
        String url = "https://v1.hitokoto.cn?c=i";
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(new HttpHeaders()),
                String.class
        );

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        } else {
            throw new RuntimeException("Failed to fetch hitokoto: " + responseEntity.getStatusCode());
        }
    }
}
