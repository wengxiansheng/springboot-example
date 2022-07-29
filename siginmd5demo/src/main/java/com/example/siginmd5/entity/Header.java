package com.example.siginmd5.entity;

import lombok.Builder;
import lombok.Data;

/**
 * @author wengly
 * @date 2022-05-26 11:08:03
 */
@Data
@Builder
public class Header {
    public String appId;
    public String nonce;
    public String sign;
    public String timestamp;
    public String appSign;

    public Header(String appId, String nonce, String sign, String timestamp, String appSign) {
        this.appId = appId;
        this.nonce = nonce;
        this.sign = sign;
        this.timestamp = timestamp;
        this.appSign = appSign;
    }
}
