package com.controller;
import	java.util.concurrent.TimeUnit;

import com.IAddDelayedTask;
import com.enums.BusinessTypeEnum;
import com.loopPull.LoopPullDelayedTaskListener;
import com.passiveMonitor.AddMonitorDelayedTask;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.concurrent.locks.LockSupport;

@Controller
@RequestMapping("test")
public class TestController {


    @Resource
    private LoopPullDelayedTaskListener loopPullDelayedTaskListener;

    @Resource
    private IAddDelayedTask addMonitorDelayedTask;

    @Resource
    private IAddDelayedTask addLoopPullDelayedTask;


    @RequestMapping("/start")
    @ResponseBody
    public String starting(){
        Thread t = loopPullDelayedTaskListener.getLoopPullDelayedTaskThread(BusinessTypeEnum.opendAccount);
        if(t.getState()== Thread.State.WAITING){
            LockSupport.unpark(t);
            return "已成功唤醒";
        }else{
            return "正在运行着了";
        }
    }

    @RequestMapping("/addMonitorDelayedTask")
    @ResponseBody
    public String addMonitorDelayedTask(String topic){
        addMonitorDelayedTask.addDelayedTask(topic,"5555",10,TimeUnit.SECONDS);
        return "success";
    }

    @RequestMapping("/addLoopPullDelayedTask")
    @ResponseBody
    public String addLoopPullDelayedTask(String topic){
        addLoopPullDelayedTask.addDelayedTask(topic,"6666",10,TimeUnit.SECONDS);
        return "success";
    }
}