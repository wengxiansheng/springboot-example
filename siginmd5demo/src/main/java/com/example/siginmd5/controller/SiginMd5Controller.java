package com.example.siginmd5.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.siginmd5.entity.APIRequestEntity;
import com.example.siginmd5.entity.Header;
import com.example.siginmd5.entity.UserEntity;
import com.example.siginmd5.util.SiginUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * @author wengly
 * @date 2022-06-27 15:19:49
 */
@RestController
@Slf4j
public class SiginMd5Controller {
    @Resource
    public SiginUtil siginUtil;
    @PostMapping("/serverVerify")
    public String serverVerify(@RequestBody String requestParam, HttpServletRequest request) throws Exception{

        APIRequestEntity apiRequestEntity = JSONObject.parseObject(requestParam, APIRequestEntity.class);
//        Header header = apiRequestEntity.getHeader();
        UserEntity userEntity = JSONObject.parseObject(JSONObject.toJSONString(apiRequestEntity.getBody()), UserEntity.class);
        // 从header中获取相关信息，其中appSecret需要自己根据传过来的appId来获取
        String appId = request.getHeader("appId");
        String appSecret = siginUtil.getAppSecret(appId);
        String nonce = request.getHeader("nonce");
        String timestamp = request.getHeader("timestamp");
        String appSign = request.getHeader("appSign");
        String signHeader = request.getHeader("sign");
        // 首先，拿到参数后同样进行签名
        String sign = siginUtil.getSHA256Str(JSONObject.toJSONString(userEntity));
        if (!sign.equals(signHeader)) {
            throw new Exception("数据签名错误！");
        }


        // 从header中获取相关信息，其中appSecret需要自己根据传过来的appId来获取
//        String appId = header.getAppId();
//        String appSecret = siginUtil.getAppSecret(appId);
//        String nonce = header.getNonce();
//        String timestamp = header.getTimestamp();

        // 按照同样的方式生成appSign，然后使用公钥进行验签
        Map<String, String> data = Maps.newHashMap();
        data.put("appId", appId);
        data.put("nonce", nonce);
        data.put("sign", sign);
        data.put("timestamp", timestamp);
        Set<String> keySet = data.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k : keyArray) {
            if (data.get(k).trim().length() > 0) // 参数值为空，则不参与签名
                sb.append(k).append("=").append(data.get(k).trim()).append("&");
        }
        sb.append("appSecret=").append(appSecret);

        if (!siginUtil.rsaVerifySignature(sb.toString(), siginUtil.appKeyPair.get(appId).get("publicKey"), appSign)) {
            throw new Exception("公钥验签错误！");
        }

        log.info("【提供方】验证通过！");

        return "【提供方】验证通过！";
    }
}
