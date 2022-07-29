package com.example.siginmd5.entity;

import com.example.siginmd5.entity.UserEntity;
import lombok.Data;

/**
 * @author wengly
 * @date 2022-05-26 10:51:29
 */
@Data
public class APIRequestEntity {
    public Header header;
    public UserEntity body;
}
