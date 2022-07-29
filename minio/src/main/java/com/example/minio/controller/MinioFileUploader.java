package com.example.minio.controller;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author wly
 * @date 2022-05-23 8:45:27
 * @describe java minio 创建桶，删除桶及文件上传，下载及删除
 */
@Slf4j
@RestController
public class MinioFileUploader {
    //创建桶
    @GetMapping("creatBuckett_1")
    public String creatBucket_1(){
        log.info("开始创建桶！");
        String name="datatest";
        String result="创建桶成功，桶名称："+name;
        try {
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint("https://play.min.io")//minio 服务器地址
                            .credentials("minioadmin","minioadmin")//minio 账号密码
                            .build();
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket("datatest").build());
            if (!found) {
                // Make a new bucket called 'datatest'.
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("datatest").build());
            } else {
                log.info("Bucket 'datatest' already exists.");
            }
            log.info(result);
            return result;
        }catch (Exception e){
            log.info("创建桶失败！");
            return "创建桶失败！";
        }
    }
    //删除桶
    @GetMapping("removeBuckett_1")
    public String removeBuckett_1() {
        log.info("开始删除桶！");
        String name="datatest";
        String result="删除桶成功，桶名称："+name;
        try {
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint("https://play.min.io")//minio 部署服务器地址
                            .credentials("minioadmin","minioadmin")//minio 账号密码
                            .build();
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket("datatest").build());
            if (found) {
                // remove a bucket called 'datatest'.
                minioClient.removeBucket(RemoveBucketArgs.builder().bucket("datatest").build());
            } else {
                log.info("Bucket 'datatest' already exists.");
            }
            log.info(result);
            return result;
        }catch (Exception e){
            log.info("删除桶失败！");
            return "删除桶失败！";
        }
    }
    //文件上传
    @GetMapping("fileUploadert_1")
    public String fileUploadert_1(){
        String path="D://data/hello.txt";
        log.info("开始进行文件上传，文件名称：hello.txt");
        try {
            MinioClient minioClient =
                    MinioClient.builder()
                            .endpoint("https://play.min.io")//minio 部署服务器地址
                            .credentials("minioadmin","minioadmin")//minio 账号密码
                            .build();
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket("datatest").build());
            if (!found) {
                // Make a new bucket called 'datatest'.
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("datatest").build());
            } else {
                log.info("Bucket 'datatest' already exists.");
            }
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket("datatest")  //minio 桶名称
                            .object("hello.txt") //minio服务器上的保存文件名称
                            .filename(path)  //本地文件路径
                            .build());
            log.info(path);
        }catch (Exception e){
            log.info("文件上传失败！");
        }
        return "文件上传成功，上传到桶位置是：datatest，上传成功文件是："+path;
    }
    //下载文件
    @GetMapping("downloadFilest_1")
    public String downloadFilest_1() throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, ErrorResponseException {
        String path="D://data/";
        String bucketName = "datatest";
        log.info("开始进行文件下载，文件名称：hello.txt");
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint("https://play.min.io")//minio 部署服务器地址
                        .credentials("minioadmin","minioadmin")//minio 账号密码
                        .build();
        // 检查'datatest'是否存在。
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (found) {
            // 列出'my-bucketname'里的对象
            Iterable<Result<Item>> myObjects = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
              //下载minio指定目录下的所有文件
//            for (Result<Item> result : myObjects) {
//                Item item = result.get();
//                log.info(item.lastModified() + ", " + item.size() + ", " + item.objectName());
//
//                minioClient.downloadObject(
//                        DownloadObjectArgs.builder()
//                                .bucket(bucketName)
//                                .object(item.objectName())
//                                .filename(path+item.objectName())
//                                .build());
//            }
            //下载minio指定目录下的某个文件
            minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket(bucketName)
                            .object("hello.txt")
                            .filename(path+"hello.txt")
                            .build());
            log.info("文件下载成功，文件保存路径是："+path+",下载文件名称是：hello.txt");
        }
        return "文件下载成功，文件保存路径是："+path+",下载文件名称是：hello.txt";
    }
    //删除指定文件
    @GetMapping("removeFilest_1")
    public String removeFilest_1(){
        String bucketName = "datatest";
        log.info("开始进行删除指定文件！");
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint("https://play.min.io")//minio 部署服务器地址
                        .credentials("minioadmin","minioadmin")//minio 账号密码
                        .build();
        // 检查'datatest'是否存在。
        try{
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (found) {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object("hello.txt")
                                .build());

            }
        }catch (Exception e){
            log.info("删除指定文件失败！");
        }
        return "文件删除成功，删除文件名称是：hello.txt";
    }
}
