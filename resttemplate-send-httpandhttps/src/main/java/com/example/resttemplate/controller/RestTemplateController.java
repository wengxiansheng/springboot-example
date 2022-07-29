package com.example.resttemplate.controller;


import com.example.resttemplate.util.httpRequestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


/**
 * @author wengly
 * @date 2022-06-24 09:47:02
 */
@RestController
@Slf4j
public class RestTemplateController {
    @Autowired
    public httpRequestUtil httpRequestUtil;
    @Autowired
    private ObjectMapper objectMapper;
    private String applicationJson = "application/json";
    private String applicationStr = "application/X-WWW-form-urlencoded";
    @GetMapping("/test")
    public String test() {
        StringBuilder userUrl = new StringBuilder("http://10.10.10.10:8010/assist/userinfo")
                .append("?usrtId=").append("111111")
                .append("&name=").append("zhangsan");
        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.put( "notesId","1001");
        rootNode.put( "fex", "女");
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Content-Type", applicationJson);
            HttpEntity httpEntity = new HttpEntity(rootNode.toString(),headers);
            String result = httpRequestUtil.postRequest(userUrl.toString() , httpEntity, String.class);
            return result;
        }catch (Exception e){
            log.error("调用接口失败:"+e.getMessage());
            return null;
        }
    }
}
