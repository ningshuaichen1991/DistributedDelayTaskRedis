package com.distributedDelayTaskRedis;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import	java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import com.IAddDelayedTask;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class TestDistributedDelayTaskRedis {

    @Resource
    IAddDelayedTask addMonitorDelayedTask;

    @Resource
    IAddDelayedTask addLoopPullDelayedTask;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Test
    public void addMonitorDelayedTaskTest() throws Exception{
        addMonitorDelayedTask.addDelayedTask("openAccount","1234",10,TimeUnit.SECONDS);
        Thread.sleep(200000);
    }


    @Test
    public void addLoopPullDelayedTask() throws Exception{
        addLoopPullDelayedTask.addDelayedTask("openAccount","12345",20,TimeUnit.SECONDS);
        //Thread.sleep(200000);
    }

//1.585219869E9
    @Test
    public void removeZSet(){
        //addLoopPullDelayedTask.addDelayedTask("openAccount","1234",20,TimeUnit.SECONDS);
        BoundZSetOperations boundZSetOperations = stringRedisTemplate.boundZSetOps("openAccount");
        Set<ZSetOperations.TypedTuple> scoreSets =  boundZSetOperations.rangeWithScores(0,0);
        ZSetOperations.TypedTuple tuple = (ZSetOperations.TypedTuple) scoreSets.toArray()[0];
        double score = tuple.getScore();
        String value = (String)tuple.getValue();
        LocalDateTime localDateTime = LocalDateTime.now();
        long times = (localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        if(times>=score){
            System.out.println("score："+score+"，value："+value);
            boundZSetOperations.remove(value);
        }
    }
}