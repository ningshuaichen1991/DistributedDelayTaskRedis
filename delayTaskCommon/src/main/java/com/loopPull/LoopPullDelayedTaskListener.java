package com.loopPull;
import java.util.concurrent.*;
import	java.util.concurrent.locks.LockSupport;

import com.IDelayedTask;
import com.enums.BusinessTypeEnum;
import com.gateWay.DelayTaskBusinessGateWay;
import com.taskBusiness.IDelayedTaskBusiness;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * 循环拉取的方式监听
 */
@Component
@Slf4j
public class LoopPullDelayedTaskListener {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private DelayTaskBusinessGateWay delayTaskGateWay;

    @Resource
    private IDelayedTask loopPullDelayedTaskService;

    private static ExecutorService executors = Executors.newCachedThreadPool();

    private static Map<BusinessTypeEnum,Thread> redisDelayTaskThreadMap = new ConcurrentHashMap<>();

    private final  int DEFAULT_EMPTY_POLL_COUNT = 100;

    public Thread getLoopPullDelayedTaskThread(BusinessTypeEnum businessTypeEnum){
        return redisDelayTaskThreadMap.get(businessTypeEnum);
    }

    @PostConstruct
    private void runLoopPullDelayedTask(){
        List<BusinessTypeEnum> businessTypeEnumList = BusinessTypeEnum.getAllBusinessTypeList();
        for(BusinessTypeEnum businessTypeEnum:businessTypeEnumList){
            Thread thread = new Thread(new LoopPullDelayedTaskRunnable(businessTypeEnum));
            thread.start();
            redisDelayTaskThreadMap.put(businessTypeEnum,thread);
        }
    }

    class LoopPullDelayedTaskRunnable implements Runnable {

        private BusinessTypeEnum businessTypeEnum;

        volatile int loopCount;

        LoopPullDelayedTaskRunnable(BusinessTypeEnum businessTypeEnum){
            this.businessTypeEnum = businessTypeEnum;
        }

        @Override
        public void run(){
            while (true){
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException e) {
                   log.error("InterruptedException：",e);
                }
                if(loopCount==DEFAULT_EMPTY_POLL_COUNT){//如果空轮询的次数超过了300次则wait
                    log.info("topic：{}开始进入等待阶段",businessTypeEnum.getBusinessValue());
                    LockSupport.park();
                    loopCount=0;//如果有值或者被释放开则恢复初始值
                }
                BoundZSetOperations boundZSetOperations = stringRedisTemplate.boundZSetOps(businessTypeEnum.getBusinessValue());
                if(boundZSetOperations.zCard()==0){
                    log.info("topic：{}，还没有延时任务",businessTypeEnum.getBusinessValue());
                    loopCount++;
                    continue;
                }
                log.info("topic：{}，延时任务已经在监控中……",businessTypeEnum.getBusinessValue());
                loopCount=0;
                Set<ZSetOperations.TypedTuple> scoreSets =  boundZSetOperations.rangeWithScores(0,0);
                ZSetOperations.TypedTuple tuple = (ZSetOperations.TypedTuple) scoreSets.toArray()[0];
                double score = tuple.getScore();
                String value = (String)tuple.getValue();
                LocalDateTime localDateTime = LocalDateTime.now();
                long times = (localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                if(times>=score){
                    log.info("已从zSet中取出，开始是执行topic："+businessTypeEnum.getBusinessValue()+"，value："+value);
                    IDelayedTaskBusiness lisenter =  delayTaskGateWay.getDelayedTaskLisenter(businessTypeEnum.getBusinessValue());
                    executors.execute(()->lisenter.execute(businessTypeEnum.getBusinessValue(),value,loopPullDelayedTaskService));//异步执行任务防止堵塞主线程
                    boundZSetOperations.remove(value);
                }
            }
        }
    }
}