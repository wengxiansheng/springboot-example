package com.example.operationsftp.entity;

import lombok.Data;

/**
 * @author wengly
 * @date 2022-06-30 17:43:16
 */
@Data
public class FileEntity {
    private String loginName;
    private String loginPassword;
    private String server;
    private Integer port;
    private String edipFolder;

    public FileEntity(String loginName, String loginPassword, String server, Integer port, String edipFolder) {
        this.loginName = loginName;
        this.loginPassword = loginPassword;
        this.server = server;
        this.port = port;
        this.edipFolder = edipFolder;
    }
}
