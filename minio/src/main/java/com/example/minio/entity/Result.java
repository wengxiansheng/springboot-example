package com.example.minio.entity;

import lombok.Data;

/**
 * @author wengly
 * @date 2022-06-22 09:00:39
 */
@Data
public class Result <T> {
    public static Integer SUCCESS = 200;
    public static Integer ERROR = -1;
    public Integer code;
    public T data;
    public String msg;

    public Result() {
    }

    public Result(Integer code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public Result(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
