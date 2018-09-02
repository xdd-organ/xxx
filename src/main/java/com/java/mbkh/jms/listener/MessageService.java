package com.java.mbkh.jms.listener;

import com.alibaba.fastjson.JSONObject;
import com.java.common.jms.MqServiceImpl;
import com.java.mbkh.server.manager.ChannelManager;
import io.netty.channel.group.ChannelGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("messageService")
public class MessageService extends MqServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    private ChannelManager channelManager;

    @Override
    public void doService(String jsonStr) {
        LOGGER.info("接收到消息：{}", jsonStr);
        Map<String, String> jsonMap = JSONObject.parseObject(jsonStr, Map.class);
        String uid = jsonMap.get(jsonMap.get("UID"));
        int i = channelManager.sendMsgByUid(uid, jsonStr);
        if (i == 1) {
            LOGGER.info("发送消息成功，uid；{}", uid);
        } else {
            LOGGER.info("发送消息失败，uid；{}", uid);
        }
    }
}