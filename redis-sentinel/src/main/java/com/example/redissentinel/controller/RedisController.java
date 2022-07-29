package com.example.redissentinel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wengly
 * @date 2022-06-21 11:13:32
 */
@RestController
public class RedisController {

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @GetMapping("get")
    public String get(String key){

        return stringRedisTemplate.opsForValue().get(key);
    }

    @RequestMapping("put")
    public String put(String key,String value){

        stringRedisTemplate.opsForValue().set (key,value);
        return get(key);
    }
}
