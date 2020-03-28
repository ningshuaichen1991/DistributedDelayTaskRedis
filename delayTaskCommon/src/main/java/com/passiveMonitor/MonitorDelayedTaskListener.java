package com.passiveMonitor;

import com.IDelayedTask;
import com.enums.BusinessTypeEnum;
import com.gateWay.DelayTaskBusinessGateWay;
import com.taskBusiness.IDelayedTaskBusiness;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 被动监听的方式进行延时任务的处理
 */
@Component
@Slf4j
public class MonitorDelayedTaskListener extends KeyExpirationEventMessageListener {

    @Resource
    private DelayTaskBusinessGateWay delayTaskBusinessGateWay;

    @Resource
    private IDelayedTask monitorDelayedTaskService;

    private static ExecutorService executors = Executors.newCachedThreadPool();//执行延时任务逻辑的线程池


    public MonitorDelayedTaskListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    /**
     * 针对redis数据失效事件，进行数据处理
     * 单机模式下测试推送正常，但是到线上的时候，用户收到了两条推送，原因是因为服务开了集群，
     * key失效的时候每个服务都收到了通知，这时候进行消息的推送，所以发生了推送多条消息的问题,可以用分布式锁避免
     * @param message
     * @param pattern
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        // 用户做自己的业务处理即可,注意message.toString()可以获取失效的key
        String expiredKey = message.toString();
        //设置监听频道
        String keyArray[]  = expiredKey.split("_");
        if(!BusinessTypeEnum.getAllBusinessTypeStringList().contains(keyArray[0])){
            return;
        }
        IDelayedTaskBusiness lisenter =  delayTaskBusinessGateWay.getDelayedTaskLisenter(keyArray[0]);
        executors.execute(()->lisenter.execute(keyArray[0],keyArray[1],monitorDelayedTaskService));
    }
}