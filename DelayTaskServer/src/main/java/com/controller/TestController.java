package com.controller;
import	java.util.concurrent.TimeUnit;

import com.IDelayedTask;
import com.enums.BusinessTypeEnum;
import com.loopPull.LoopPullDelayedTaskListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.concurrent.locks.LockSupport;

@Slf4j
@Controller
@RequestMapping("test")
public class TestController {


    @Resource
    private LoopPullDelayedTaskListener loopPullDelayedTaskListener;

    @Resource
    private IDelayedTask addMonitorDelayedTask;

    @Resource
    private IDelayedTask addLoopPullDelayedTask;


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

    /**
     * 添加被动监听的任务
     * @param topic
     * @return
     */
    @RequestMapping("/addMonitorDelayedTask")
    @ResponseBody
    public String addMonitorDelayedTask(String topic){
        addMonitorDelayedTask.addDelayedTask(topic,"1",10,TimeUnit.SECONDS);
        return "success";
    }


    /**
     * 自己系统的开户操作
     * @return
     */
    @RequestMapping("/toOpenAccount")
    public String toOpenAccount(){
        log.info("本系统开户操作………………");
        return "openAccount";
    }

    /**
     * 添加主动拉取循环的任务
     * @param topic
     * @return
     */
    @RequestMapping("/addLoopPullDelayedTask")
    @ResponseBody
    public String addLoopPullDelayedTask(String topic){
        addLoopPullDelayedTask.addDelayedTask(topic,"1",10,TimeUnit.SECONDS);
        return "success";
    }


    /**
     * 跳转到的开户的H5界面
     * @return
     */
    @RequestMapping("/toJumpOpenAccountH5")
    public String toJumpOpenAccountH5(String type){
        if(type.equals("monitor")){//被动监听任务添加
            addMonitorDelayedTask.addDelayedTask(BusinessTypeEnum.opendAccount.getBusinessValue(),"1",300,TimeUnit.SECONDS);
        }else{//主动拉取任务添加
            addLoopPullDelayedTask.addDelayedTask(BusinessTypeEnum.opendAccount.getBusinessValue(),"1",300,TimeUnit.SECONDS);
        }
        log.info("H5存管开户跳转………………");
        return "depositAccountOpenH5";
    }

    /**
     * 开户结果回调通知接口
     * @return
     */
    @RequestMapping("/notifyResultMonitor")
    @ResponseBody
    public String notifyResult(){
        addMonitorDelayedTask.removeDelayedTask(BusinessTypeEnum.opendAccount.getBusinessValue(),"1");
        log.info("开户结果通知成功……");
        return "success";
    }

    /**
     * 开户结果回调通知接口
     * @return
     */
    @RequestMapping("/notifyResultLoopPull")
    @ResponseBody
    public String notifyResultLoopPull(){
        addLoopPullDelayedTask.removeDelayedTask(BusinessTypeEnum.opendAccount.getBusinessValue(),"1");
        log.info("开户结果通知成功……");
        return "success";
    }
}