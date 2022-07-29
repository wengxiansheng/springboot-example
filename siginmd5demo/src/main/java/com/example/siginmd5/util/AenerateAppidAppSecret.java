package com.example.siginmd5.util;

import cn.hutool.core.util.IdUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
/**
 * @author wengly
 * @date 2022-06-27 11:37:54
 */
@Component
@Slf4j
public class AenerateAppidAppSecret {
    public Map<String, String> appMap = Maps.newConcurrentMap();
    /**
     * @param
     * @describe 生成Appid
     * @return
     */
    public String aenerateAppid(){
        //使用雪花算法生成id
        String appId = IdUtil.simpleUUID();
        return appId;
    }
    /**
     * @param appId
     * @describe 生成AppSecret
     * @return
     */
    public void aenerateAppSecret(String appId){
        appMap.put(appId, UUID.randomUUID().toString());
    }
    /**
     * @param appId
     * @describe 获取AppSecret
     * @return
     */
    public String getAppSecret(String appId){
        return String.valueOf(appMap.get(appId));
    }
}
