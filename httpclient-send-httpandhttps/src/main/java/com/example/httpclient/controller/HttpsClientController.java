package com.example.httpclient.controller;

import com.example.httpclient.util.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author wengly
 * @date  2022-06-24 09:45:59
 */
@RestController
@Slf4j
public class HttpsClientController {
    @Autowired
    private HttpClientUtils httpClientUtils;
    private String applicationJson = "application/json";
    private String applicationStr = "application/X-WWW-form-urlencoded";
    @GetMapping("/testGet")
    public String testGet(String a) {
        return "Get-调用成功!";
    }
    @PutMapping("/testPut")
    public String testPut(String a) {
        return "Put-调用成功!";
    }
    @DeleteMapping("/testDelete")
    public String testDelete(String a) {
        return "Delete-调用成功!";
    }
    @PostMapping("/testPost")
    public String testPost(String a) {
        return "Post-调用成功!";
    }
    @GetMapping("/test")
    public String testtre() throws IOException {
        Map<String, String> parm = new HashMap<String, String>();
        parm.put("a","123");
        String wxResult = httpClientUtils.get("http://127.0.0.1:8080/testGet",parm);
//        String wxResult = httpClientUtils.put("http://127.0.0.1:8010/testPut",parm);
//        String wxResult = httpClientUtils.delete("http://127.0.0.1:8010/testDelete",parm);
//        String wxResult = httpClientUtils.post("http://127.0.0.1:8010/testPost",parm,parm);
        return wxResult;
    }
}
