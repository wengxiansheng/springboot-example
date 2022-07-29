package com.example.minio.util;

import io.minio.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;


/**
 * @author wengly
 * @date 2022-06-21 15:19:37
 */
@Component
@Slf4j
public class MinioUtil {

    @Autowired
    private MinioClient minioClient;

    /**
     * 创建bucket
     * @param bucketName  桶名称
     * */
    public String creatBucket(String bucketName){
        log.info("开始创建桶！");
        String result="创建桶成功，桶名称："+bucketName;
        try {
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                // Make a new bucket called 'datatest'.
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            } else {
                log.info("Bucket "+bucketName+" already exists.");
            }
            log.info(result);
            return result;
        }catch (Exception e){
            log.info("创建桶失败！"+e.getMessage());
            return "创建桶失败！";
        }
    }
    /**
     * 删除bucket
     * @param bucketName  桶名称
     * */
    public String removeBucket(String bucketName) {
        log.info("开始删除桶！");
        String result="删除桶成功，桶名称："+bucketName;
        try {
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (found) {
                // remove a bucket called 'datatest'.
                minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
            } else {
                log.info("Bucket "+bucketName+" already exists.");
            }
            log.info(result);
            return result;
        }catch (Exception e){
            log.info("删除桶失败！"+e.getMessage());
            return "删除桶失败！";
        }
    }

    /**
     * 文件上传(上传给定的流作为存储桶中的对象。)
     * @param bucketName  桶名称
     * @param inputStream  文件流
     * @param fileName     文件名称
     * */
    public boolean upload(String bucketName,InputStream inputStream,String fileName){
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(inputStream,inputStream.available(),-1)
                    .build());

        }catch (Exception e){
            log.error(e.getMessage());
            return false;
        }finally {
            if(inputStream !=null){
                try {
                    inputStream.close();
                }catch (IOException e){
                    log.error(e.getMessage());
                }
            }

        }
        return true;

    }
    /**
     * 文件上传(将文件中的内容作为存储桶中的对象上传。)
     * @param bucketName    桶名称
     * @param fileName     文件名称
     * @param filePath     本地文件存放路径
     * */
    public String fileUploader(String bucketName,String fileName,String filePath){
        log.info("开始进行文件上传，文件名称："+fileName);
        try {

            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                // Make a new bucket called 'datatest'.
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            } else {
                log.info("Bucket "+bucketName+" already exists.");
            }
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucketName)  //minio 桶名称
                            .object(fileName) //minio服务器上的保存文件名称
                            .filename(filePath+fileName)  //本地文件路径
                            .build());
            log.info(filePath+fileName);
        }catch (Exception e){
            log.info("文件上传失败！");
        }
        return "文件上传成功，上传到桶位置是：datatest，上传成功文件是："+filePath+fileName;
    }
    /**
     * 文件下载(获取对象流的数据。InputStream使用后必须关闭返回以释放网络资源。)
     * @param bucketName  桶名称
     * @param fileName     文件名称
     * */
    public byte[] download(String bucketName,String fileName){
        InputStream inputStream = null;
        try {
            inputStream = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(fileName).build());
            byte[] bytes = IOUtils.toByteArray(inputStream);
            return bytes;
        }catch (IOException e){
            log.error(e.getMessage());

        }catch (Exception e){
            log.error(e.getMessage());

        }finally {
            if(inputStream !=null){
                try {
                    inputStream.close();
                }catch (IOException e){
                    log.error(e.getMessage());
                }
            }

        }
        return null;
    }

    /**
     * 文件下载(将对象的数据下载到文件中。),下载指定文件
     * @param bucketName   桶名称
     * @param fileName     文件名称
     * @param filePath     文件存放本地路径
     * */
    public String downloadFiles(String bucketName,String fileName,String filePath) {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (found) {
                //下载minio指定目录下的某个文件
                minioClient.downloadObject(
                        DownloadObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileName)//minio服务器上的保存文件名称
                                .filename(filePath+fileName)//文件存放本地路径
                                .build());
                log.info("文件下载成功，文件保存路径是："+filePath+",下载文件名称是："+fileName);
            }
            return "文件下载成功，文件保存路径是："+filePath+",下载文件名称是："+fileName;
        }catch (Exception e){
            log.error(e.getMessage());
            return "文件下载失败"+e.getMessage();
        }
    }
    /**
     * 文件下载(将对象的数据下载到文件中。),桶下所有文件
     * @param bucketName   桶名称
     * @param fileName     文件名称
     * @param filePath     文件存放本地路径
     * */
    public String downloadAllFiles(String bucketName,String fileName,String filePath) {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (found) {
                // 列出'my-bucketname'里的对象
                Iterable<Result<Item>> myObjects = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
                //下载minio指定目录下的所有文件
                for (Result<Item> result : myObjects) {
                    Item item = result.get();
                    log.info(item.lastModified() + ", " + item.size() + ", " + item.objectName());

                    minioClient.downloadObject(
                            DownloadObjectArgs.builder()
                                    .bucket(bucketName)
                                    .object(item.objectName())
                                    .filename(filePath+item.objectName())
                                    .build());
                    log.info("文件下载成功，文件保存路径是："+filePath+",下载文件名称是："+fileName);
                }

            }
            return "文件下载成功";

        }catch (Exception e){
            log.error(e.getMessage());
            return "文件下载失败"+e.getMessage();
        }
    }
    /**
     * 文件删除(删除指定文件。)
     * @param bucketName   桶名称
     * @param fileName     文件名称
     * */
    public String removeFiles(String bucketName,String fileName){
        log.info("开始进行删除指定文件！");
        try{
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (found) {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileName)
                                .build());
            }
        }catch (Exception e){
            log.info("删除指定文件失败！");
        }
        return "文件删除成功，删除文件名称是："+fileName;
    }
    /**
     * 文件删除(删除整个bucket下的文件。)
     * @param bucketName   桶名称
     * @param fileName     文件名称
     * */
    public String removeAllFiles(String bucketName,String fileName){
        try {
            // 列出'my-bucketname'里的对象
            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
            Iterator<Result<Item>> iterator = results.iterator();
            while (iterator.hasNext()){
                Item item = iterator.next().get();
                minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(item.objectName()).build());

            }
            return "文件删除成功";

        }catch (Exception e){
            log.error("删除失败："+e.getMessage());
            return "文件删除失败："+e.getMessage();

        }
    }

}
