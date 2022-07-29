package com.example.resttemplate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author wly
 * @date 2022-05-17 14:59:30
 * *RestTemplateConfig配置文件
 */
@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate(){
        //同时支持http和https请求方式
        RestTemplate restTemplate = new RestTemplate (new HttpsClientRequestFactory());
        List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
        //重新设置StringHttpMessageConverter字符集为UTF-8，解决中文乱码问题
        HttpMessageConverter<?> converterTarget = null;
        for (HttpMessageConverter<?> item : converterList){
            if (StringHttpMessageConverter.class == item.getClass()){
                converterTarget = item;
                break;
            }
        }
        if (null != converterTarget){
            converterList.remove(converterTarget);
        }

        converterList.add(1,new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }
}
