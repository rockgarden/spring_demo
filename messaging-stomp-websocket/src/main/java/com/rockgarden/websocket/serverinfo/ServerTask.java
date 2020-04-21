package com.rockgarden.websocket.serverinfo;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONUtil;
import com.rockgarden.websocket.WebSocketConsts;
import com.rockgarden.websocket.serverinfo.model.Server;
import com.rockgarden.websocket.serverinfo.payload.ServerVO;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 服务器定时推送任务
 */
@Slf4j
/*
 * Define log with @Slf4j to omit the following code:
 * 
 * private static final Logger log =
 * LoggerFactory.getLogger(ScheduledTasks.class);
 */
@Component
public class ServerTask {
    @Autowired
    private SimpMessagingTemplate wsTemplate;

    /*
     * 
     * fixedRate, which specifies the interval between method invocations, measured
     * from the start time of each invocation.
     * 
     * fixedDelay, which specifies the interval between invocations measured from
     * the completion of the task.
     * 
     * @Scheduled(cron=". . .") expressions for more sophisticated task scheduling.
     */
    @Scheduled(cron = "0/2 * * * * ?")
    public void websocket() throws Exception {
        log.info("【推送消息】开始执行：{}", DateUtil.formatDateTime(new Date()));
        // 查询服务器状态
        Server server = new Server();
        server.copyTo();
        ServerVO serverVO = ServerUtil.wrapServerVO(server);
        Dict dict = ServerUtil.wrapServerDict(serverVO);
        wsTemplate.convertAndSend(WebSocketConsts.PUSH_SERVER, JSONUtil.toJsonStr(dict));
        log.info("【推送消息】执行结束：{}", DateUtil.formatDateTime(new Date()));
    }

    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        log.info("The time is now {}", DateUtil.formatDateTime(new Date()));
    }
}
