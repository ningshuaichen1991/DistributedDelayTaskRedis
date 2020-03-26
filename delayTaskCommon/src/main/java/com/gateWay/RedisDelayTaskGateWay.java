package com.gateWay;

import com.annotation.DelayTaskType;
import com.taskListener.IDelayedTaskLisenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class RedisDelayTaskGateWay{

    @Autowired
    private List<IDelayedTaskLisenter> delayedTaskLisenterList;


    public IDelayedTaskLisenter getDelayedTaskLisenter(String topic){
        for(IDelayedTaskLisenter service : delayedTaskLisenterList){
            DelayTaskType delayTaskType = service.getClass().getAnnotation(DelayTaskType.class);
            if(delayTaskType.topic().getBusinessValue().equals(topic)){
                log.info("已导航到具体的延时任务监听器类，{}",service.getClass().getSimpleName());
                return service;
            }
        }
       throw new RuntimeException("topic出错~~~");
    }
}
