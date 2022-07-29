package com.example.operationsftp.service;


import com.example.operationsftp.entity.FileEntity;
import com.example.operationsftp.utils.SftpUtil;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Calendar;
import java.util.Map;

/**
 * @author wengly
 * @date 2022-06-30 17:42:40
 */
@Slf4j
@Service
public class SftpService {
   @Value("${sftp.loginName}")
    private String loginName;
    @Value("${sftp.loginPassword}")
    private String loginPassword;
    @Value("${sftp.server}")
    private String server;
    @Value("${sftp.port}")
    private Integer port;
    @Value("${edip.folder}")
    private String edipFolder;

    @Autowired
    private SftpUtil sftpUtil;

    private Map<String,String> edipFileMap = null;

    //通过SFTP从服务器下载文件
    public void getEdipFileMap(){
        FileEntity fileEntity= new FileEntity(loginName,loginPassword, server, port,edipFolder);
        ChannelSftp connect = sftpUtil.connect(fileEntity);
        try{
           edipFileMap = sftpUtil.getFile(connect,fileEntity);
//           sftpUtil.deleteFile(connect,fileEntity);
           if (!edipFileMap.isEmpty()){
               //删除本地缓存
               Calendar calendar = Calendar.getInstance();
//               int mouth = calendar.get(Calendar.MONTH);
//               calendar.set(Calendar.MONTH,mouth - 1);
               int day = calendar.get (Calendar.DAY_OF_MONTH);
               calendar.set(Calendar.DAY_OF_MONTH, day - 30);
               File file = new File(edipFolder);
               if (file.exists()) {
                  for (File folder : file.listFiles()){
                     if (folder.lastModified() < calendar.getTimeInMillis()){
                        for (File ListFile : folder.listFiles()){
                            ListFile.delete();
                        }
                        folder.delete();
                     }
                  }
               }

           }

        }catch(SftpException e){
              log.error(e.getMessage(),e);
        }finally {
              sftpUtil.disconnect(connect);
        }


    }

}
