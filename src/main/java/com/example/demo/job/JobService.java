package com.example.demo.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobService {

    int i = 0;
    public void business() {
        i++;
        log.info("定时任务，业务方法执行,thread:{},i:{}", Thread.currentThread().getName(), i);
    }
}
