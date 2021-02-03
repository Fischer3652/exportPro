package com.example.demo.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Slf4j
public class BusinessJob implements Job {
    int i = 0;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String time = dataMap.get("time").toString();
        business(time);
    }

    public void business(String time) {
        i++;
        log.info("time:{},threadName:{},i:{}", time, Thread.currentThread().getName(), i);
    }
}
