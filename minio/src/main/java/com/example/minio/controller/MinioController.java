package com.example.minio.controller;

import cn.hutool.core.util.IdUtil;
import com.example.minio.entity.Result;
import com.example.minio.util.MinioUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wengly
 * @date 2022-06-21 17:48:55
 */
@Slf4j
@RestController
public class MinioController {
    public static final String bucketName = "datafile";
    @Resource
    private MinioUtil minioUtil;


    /**
     * 创建桶
     * */
    @CrossOrigin("*")//解决跨域
    @SneakyThrows
    @PostMapping("/creatBucket")
    public Result creatBucket(){
        //调用minio接口进行创建桶
        minioUtil.creatBucket(bucketName);
        Map resultMap = new HashMap<>(1);
        resultMap.put("bucketName",bucketName);
        return new Result(Result.SUCCESS,resultMap,"创建桶成功！");

    }
    /**
     * 删除桶
     * */
    @CrossOrigin("*")//解决跨域
    @SneakyThrows
    @DeleteMapping("/removeBucket")
    public Result removeBucket(){
        //调用minio接口进行删除桶
        minioUtil.removeBucket(bucketName);
        Map resultMap = new HashMap<>(1);
        resultMap.put("bucketName",bucketName);
        return new Result(Result.SUCCESS,resultMap,"删除桶成功！");

    }


    /**
     * 文件上传
     * @param file  文件流
     * */
    @CrossOrigin("*")//解决跨域
    @SneakyThrows
    @RequestMapping(value = "/uploadFile",method = RequestMethod.POST)
    public Result uploadFile(@RequestParam("file") MultipartFile file){
        if (file == null || file.isEmpty()){
            return new Result(Result.ERROR,"文件上传失败！");
        }
        String fileName = file.getOriginalFilename();
        //使用雪花算法生成id
        String fileId = IdUtil.getSnowflakeNextIdStr();
        //调用minio接口进行文件上传
        minioUtil.upload(bucketName,file.getInputStream(),fileName);
        Map resultMap = new HashMap<>(1);
        resultMap.put("fileId",fileId);
        return new Result(Result.SUCCESS,resultMap,"文件上传成功！");

    }
    /**
     * 文件下载
     * @param fileName  文件名
     * */
    @CrossOrigin("*")//解决跨域
    @SneakyThrows
    @RequestMapping(value = "/download",method = RequestMethod.POST)
    public ResponseEntity<byte[]> download(@RequestParam("fileName") String fileName){
        log.info("收到下载请求，fileName="+fileName);
        if (StringUtils.isBlank(fileName)){
            return null;
        }
        byte[] bytes = minioUtil.download(bucketName,fileName);
        if (bytes == null){
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment",fileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(bytes,headers, HttpStatus.OK);

    }
    /**
     * 删除文件
     * */
    @CrossOrigin("*")//解决跨域
    @SneakyThrows
    @DeleteMapping("/removeFiles")
    public Result removeFiles(String fileName){
        //调用minio接口进行删除桶
        minioUtil.removeFiles(bucketName,fileName);
        Map resultMap = new HashMap<>(1);
        resultMap.put("fileName",fileName);
        return new Result(Result.SUCCESS,resultMap,"文件删除成功！");

    }

}
