package com.example.zydemo.utils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @Description:
 * @Author: zhaoyi18
 * @Date: 2022/08/12 10:04
 * @Since jdk8+
 **/
@Component
@Slf4j
public class HttpUtil {
    @Autowired
    private RestTemplate restTemplate;


    public String enableRestTemplate(String url, HttpHeaders headers, String param, HttpMethod httpMethod) {
        if (headers == null) {
            headers = new HttpHeaders();
        }

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(param, headers);
        ResponseEntity<String> exchange = restTemplate.exchange(url, httpMethod, entity, String.class);
        return exchange.getBody();
    }
}
