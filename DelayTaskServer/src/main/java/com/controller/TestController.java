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

    @Resource(name="loopPullDelayedTaskService")
    private IDelayedTask loopPullDelayedTaskService;

    @Resource(name="monitorDelayedTaskService")
    private IDelayedTask monitorDelayedTaskService;


    /**
     * 添加被动监听的任务
     * @param topic
     * @return
     */
    @RequestMapping("/monitorDelayedTask")
    @ResponseBody
    public String monitorDelayedTask(String topic){
        monitorDelayedTaskService.addDelayedTask(topic,"1",10,TimeUnit.SECONDS);
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
     * 添加被动执行延时任务
     * @return
     */
    @RequestMapping("/addMonitorDelayedTask")
    @ResponseBody
    public String addMonitorDelayedTask(){
        monitorDelayedTaskService.addDelayedTask(BusinessTypeEnum.opendAccount.getBusinessValue(),"12345",10,TimeUnit.SECONDS);
        return "success";
    }

    /**
     * 添加主动拉取循环的任务
     * @return
     */
    @RequestMapping("/addLoopPullDelayedTask")
    @ResponseBody
    public String addLoopPullDelayedTask(){
        loopPullDelayedTaskService.addDelayedTask(BusinessTypeEnum.opendAccount.getBusinessValue(),"12345",10,TimeUnit.SECONDS);
        return "success";
    }


    /**
     * 跳转到的开户的H5界面
     * @return
     */
    @RequestMapping("/toJumpOpenAccountH5")
    public String toJumpOpenAccountH5(String type){
        if(type.equals("monitor")){//被动监听任务添加
            monitorDelayedTaskService.addDelayedTask(BusinessTypeEnum.opendAccount.getBusinessValue(),"12345",20,TimeUnit.SECONDS);
        }else{//主动拉取任务添加
            loopPullDelayedTaskService.addDelayedTask(BusinessTypeEnum.opendAccount.getBusinessValue(),"12345",20,TimeUnit.SECONDS);
        }
        log.info("H5存管开户跳转………………");
        return "depositAccountOpenH5";
    }

    /**
     * 被动监听回调通知接口
     * @return
     */
    @RequestMapping("/notifyResultMonitor")
    @ResponseBody
    public String notifyResult(){
        monitorDelayedTaskService.removeDelayedTask(BusinessTypeEnum.opendAccount.getBusinessValue(),"1");
        log.info("开户结果通知成功……");
        return "success";
    }

    /**
     * 主动拉取通知接口
     * @return
     */
    @RequestMapping("/notifyResultLoopPull")
    @ResponseBody
    public String notifyResultLoopPull(){
        loopPullDelayedTaskService.removeDelayedTask(BusinessTypeEnum.opendAccount.getBusinessValue(),"1");
        log.info("开户结果通知成功……");
        return "success";
    }
}