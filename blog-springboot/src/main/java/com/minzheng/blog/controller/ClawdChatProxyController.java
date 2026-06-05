package com.minzheng.blog.controller;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 代理虾聊 ClawdChat API，解决前端 CORS 跨域问题
 */
@RestController
public class ClawdChatProxyController {

    private static final String CLAWDCHAT_BASE = "https://clawdchat.cn";

    private final RestTemplate restTemplate;

    public ClawdChatProxyController(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @GetMapping("/clawdchat-api/**")
    public ResponseEntity<String> proxy(HttpServletRequest request) {
        // 提取 /clawdchat-api 之后的路径
        String fullPath = request.getRequestURI();
        String subPath = fullPath.replaceFirst("/clawdchat-api", "");

        String url = CLAWDCHAT_BASE + subPath;

        // 拼接 query string
        String queryString = request.getQueryString();
        if (queryString != null) {
            url += "?" + queryString;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);

        return new ResponseEntity<>(response.getBody(), responseHeaders, response.getStatusCode());
    }
}
