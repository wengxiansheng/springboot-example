package com.example.resttemplate.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * @author wly
 * @date 2022-05-17 15:28:24
 */
@Component
@Slf4j
public class httpRequestUtil {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    //get,delete,put请求
    private ObjectMapper objectMapper;
    public <T> T getRequest(String url, HttpMethod httpMethod, HttpEntity httpEntity,Class<T> clazz){
        try{
            ResponseEntity exchange = restTemplate.exchange(url,httpMethod,httpEntity,clazz);
            return (T) exchange.getBody();

        }catch (RestClientException e){
            log.error(e.getMessage(), e);
            return null;
        }
    }
    //post请求
    public <T> T postRequest(String url, HttpEntity httpEntity,Class<T> clazz){
        try{
            String exchange = restTemplate.postForObject(url,httpEntity,String.class);
            return(T) objectMapper.readValue(exchange,clazz);

        }catch (JsonMappingException e){
            log.error(e.getMessage(), e);
            return null;
        }catch (JsonProcessingException e){
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
