package com.example.demo.config;

import com.example.demo.job.BusinessJob;
import com.example.demo.job.JobService;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.*;

import java.time.LocalDateTime;

//@Configuration
public class QuartzConfig {

    //配置任务，多例的业务bean，耦合业务类，需要实现Job接口
    @Bean(name = "businessJobDetail")
    public JobDetailFactoryBean businessJobDetail() {
        LocalDateTime localDateTime = LocalDateTime.now();
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setJobClass(BusinessJob.class);
        //将参数传给job
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("time", localDateTime);
        jobDetailFactoryBean.setJobDataAsMap(jobDataMap);
        return jobDetailFactoryBean;
    }

    //配置任务，单例的业务bean
    @Bean(name = "jobServiceBeanDetail")
    public MethodInvokingJobDetailFactoryBean jobServiceBeanDetail(JobService jobService) {
        MethodInvokingJobDetailFactoryBean jobDetail = new MethodInvokingJobDetailFactoryBean();
        //是否并发执行
        jobDetail.setConcurrent(false);
        //需要执行的实体bean
        jobDetail.setTargetObject(jobService);
        //需要执行的方法
        jobDetail.setTargetMethod("business");
        return jobDetail;
    }

    //配置简单触发器
    @Bean(name = "simpleTrigger")
    public SimpleTriggerFactoryBean simpleTrigger(JobDetail businessJobDetail) {//businessJobDetail
        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(businessJobDetail);
        //设置启动延迟
        trigger.setStartDelay(0);
        //每隔5s执行一次
        trigger.setRepeatInterval(5000);
        return trigger;
    }

    //配置cron触发器
    @Bean(name = "cronTrigger")
    public CronTriggerFactoryBean cronTrigger(JobDetail jobServiceBeanDetail) {//目标任务jobServiceBeanDetail
        CronTriggerFactoryBean triggerFactoryBean = new CronTriggerFactoryBean();
        triggerFactoryBean.setJobDetail(jobServiceBeanDetail);
        //每隔6s执行一次
        triggerFactoryBean.setCronExpression("0/6 * * * * ?");
        return triggerFactoryBean;
    }

    //配置调用工厂，将所有的触发器引入
    @Bean(name = "scheduler")
    public SchedulerFactoryBean schedulerFactory(Trigger cronTrigger, Trigger simpleTrigger) {//需要管理的触发器cronTrigger，simpleTrigger
        SchedulerFactoryBean bean = new SchedulerFactoryBean();
        //延迟1s启动
        bean.setStartupDelay(1);
        //注册触发器，可以注册多个
        bean.setTriggers(cronTrigger, simpleTrigger);
        return bean;
    }
}
