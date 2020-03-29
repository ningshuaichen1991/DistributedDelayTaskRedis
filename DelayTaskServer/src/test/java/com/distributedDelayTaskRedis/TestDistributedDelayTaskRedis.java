package com.distributedDelayTaskRedis;
import	java.util.Scanner;
import java.util.Scanner;
import	java.util.concurrent.TimeUnit;

import com.IDelayedTask;
import com.enums.BusinessTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class TestDistributedDelayTaskRedis {

    @Resource
    IDelayedTask monitorDelayedTaskService;

    @Resource
    IDelayedTask loopPullDelayedTaskService;


    @Test
    public void addMonitorDelayedTaskTest() throws Exception{
        monitorDelayedTaskService.addDelayedTask("openAccount","1234",10,TimeUnit.SECONDS);
        Thread.sleep(60000);
    }


    @Test
    public void addLoopPullDelayedTask() throws Exception{
        loopPullDelayedTaskService.addDelayedTask(BusinessTypeEnum.opendAccount.getBusinessValue(),"12345",10,TimeUnit.SECONDS);
        Scanner sc = new Scanner(System.in);
        if(sc.hasNext()){
            String s = sc.next();
            if(s.equals("start")){
                loopPullDelayedTaskService.addDelayedTask(BusinessTypeEnum.opendAccount.getBusinessValue(),"12345",10,TimeUnit.SECONDS);
            }
        }
        Thread.sleep(200000);
    }

}