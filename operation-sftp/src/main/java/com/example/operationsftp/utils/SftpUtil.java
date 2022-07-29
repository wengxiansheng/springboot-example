package com.example.operationsftp.utils;

import com.example.operationsftp.entity.FileEntity;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author wengly
 * @date 2022-06-30 17:44:02
 */
@Configuration
@Slf4j
public class SftpUtil {
    public ChannelSftp connect(FileEntity fileEntity){
        JSch jSch = new JSch();
        Session session = null;
        try{
            session = jSch.getSession(fileEntity.getLoginName(),fileEntity.getServer(),fileEntity.getPort());
            session.setPassword(fileEntity.getLoginPassword());
            Properties sschConfig = new Properties();
            sschConfig.put("StrictHostKeyChecking","no");
            session.setConfig(sschConfig);
            session.connect();

            ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
            sftp.connect();
            log.info("连接结果：" + session.equals(sftp.getSession()));
            return sftp;
        }catch (Exception e){
            e.printStackTrace();
            log.error("SSH方式连接SFTP服务器是有JScHException异常！");
        }
        return null;
    }
    public Map<String,String> getFile(ChannelSftp sftp, FileEntity fileEntity) throws SftpException {

        Map<String,String> fileMap = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        StringBuilder sb = new StringBuilder().append(calendar.get(Calendar.YEAR))
                .append("-").append(calendar.get(Calendar.MONTH) + 1)
                .append("-").append(calendar.get(Calendar.DAY_OF_MONTH));
        File folder = new File(fileEntity.getEdipFolder() + File.separator + sb);
        if (!folder.exists()){
            folder.mkdir();
        }
        //sftp服务器上对应sftp目录
        Vector vector = sftp.ls("/data/sftp/cib/edipfiles");
        Iterator<ChannelSftp.LsEntry> iterator = vector.iterator();
        FileOutputStream output = null;
        try {
            while(iterator.hasNext()){
                ChannelSftp.LsEntry sftpFile = iterator.next();
                String filename = sftpFile.getFilename();
                if (filename.contains("orgall_V3")) {
                    output = new FileOutputStream(folder.getAbsoluteFile() +File.separator +filename);
                    sftp.get("/data/sftp/cib/edipfiles/" + filename,output);

                    if (filename.endsWith(".flg")){
                        fileMap.put("orgFilgPath",folder.getAbsolutePath() +File.separator + filename);
                    }else if (filename.endsWith(".gz")){
                        fileMap.put("orgDataPath",folder.getAbsolutePath() +File.separator + filename);
                    }

                }
                if (filename.contains("EUIP_MAINUSER_INFO")) {
                    output = new FileOutputStream(folder.getAbsoluteFile() +File.separator +filename);
                    sftp.get("/data/sftp/cib/edipfiles/" + filename,output);

                    if (filename.endsWith(".flg")){
                        fileMap.put("userFilgPath",folder.getAbsolutePath() +File.separator + filename);
                    }else if (filename.endsWith(".gz")){
                        fileMap.put("userDataPath",folder.getAbsolutePath() +File.separator + filename);
                    }

                }
                if (filename.contains("EUIP_NOTESID_INFO")) {
                    output = new FileOutputStream(folder.getAbsoluteFile() +File.separator +filename);
                    sftp.get("/data/sftp/cib/edipfiles/" + filename,output);

                    if (filename.endsWith(".flg")){
                        fileMap.put("notesFilgPath",folder.getAbsolutePath() +File.separator + filename);
                    }else if (filename.endsWith(".gz")){
                        fileMap.put("notesDataPath",folder.getAbsolutePath() +File.separator + filename);
                    }

                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(output !=null){
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return fileMap;
    }
    /**
     * 删除文件
     * @param sftp   桶名称
     * */
    public void deleteFile(ChannelSftp sftp) throws SftpException {
        //sftp服务器上对应sftp目录
        Vector vector = sftp.ls("/data/sftp/cib/edipfiles");
        Iterator<ChannelSftp.LsEntry> iterator = vector.iterator();
        iterator.forEachRemaining(sftpFile -> {
            String fileName = sftpFile.getFilename();
            if (!fileName.startsWith(".")){
                try {
                    sftp.rm("/data/sftp/cib/edipfiles/" +fileName);

                } catch (SftpException e) {
                    e.printStackTrace();
                }
                log.info("已成功删除文件："+ fileName);
            }
        });

    }
    /**
     * 关闭SSH连接
     * @param sftp   桶名称
     * */
    public void disconnect(ChannelSftp sftp){
        try {
            if (sftp != null){
                if (sftp.getSession().isConnected()){
                    sftp.getSession().disconnect();
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
