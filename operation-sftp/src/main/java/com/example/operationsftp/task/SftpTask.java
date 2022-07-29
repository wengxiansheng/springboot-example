package com.example.operationsftp.task;

import com.example.operationsftp.service.SftpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author wengly
 * @date 2022-06-30 17:41:50
 */
@Configuration
@EnableScheduling
@Slf4j
public class SftpTask implements ApplicationRunner {
    @Autowired
    private SftpService sftpService;
    @Scheduled(cron = "${task.corn}")
    public void sync(){
        sftpService.getEdipFileMap();
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {

    }
}
