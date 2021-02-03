package com.example.demo.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
//@Component
//@EnableScheduling
public class TaskService {

    //每隔1s执行一次
    @Scheduled(fixedRate = 1000)
    @Async
    public void fixMethod() {
        try {
            log.info("fixMethod,thread:{}", Thread.currentThread().getName());
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //前一个任务执行完5s后执行
    @Scheduled(fixedDelay = 5000)
    public void delayMethod() {
        log.info("delayMethod,thread:{}", Thread.currentThread().getName());
    }

    //每隔10s执行一次
    @Scheduled(cron = "0/10 * * * * ?")
    public void cronMethod() {
        log.info("cronMethod,thread:{}", Thread.currentThread().getName());
    }

}
