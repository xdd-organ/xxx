package com.java.mbkh.server.manager;

import com.java.common.redis.service.RedisService;
import com.java.mbkh.server.bean.TcpServerBean;
import com.java.mbkh.server.constant.ServerConstant;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

@Service
public class ChannelManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelManager.class);

    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Autowired
    private RedisService redisService;

    public void addChannel(Channel channel) {
        String channelId = this.getChannelId(channel);
        String host = this.getHost(channel);
        TcpServerBean tcpServerBean = new TcpServerBean(channelId, host, channel);
        redisService.hset(ServerConstant.CHANNEL_MAP.getBytes(), channelId.getBytes(), SerializationUtils.serialize(tcpServerBean));
        channels.add(channel);
    }

    public void regiestChannel(String channelId, String uid) {
        redisService.hset(ServerConstant.CHANNEL_UID_MAP, uid, channelId);
        List<byte[]> hmget = redisService.hmget(ServerConstant.CHANNEL_MAP.getBytes(), channelId.getBytes());
        if (!CollectionUtils.isEmpty(hmget)) {
            TcpServerBean tcpServerBean = SerializationUtils.deserialize(hmget.get(0));
            tcpServerBean.setUid(uid);
            redisService.hset(ServerConstant.CHANNEL_MAP.getBytes(), channelId.getBytes(), SerializationUtils.serialize(tcpServerBean));
        }
    }

    public void removeChannel(Channel channel) {
        String channelId = this.getChannelId(channel);
        channels.remove(channel);
        List<byte[]> hmget = redisService.hmget(ServerConstant.CHANNEL_MAP.getBytes(), channelId.getBytes());
        if (!CollectionUtils.isEmpty(hmget)) {
            TcpServerBean tcpServerBean = SerializationUtils.deserialize(hmget.get(0));
            redisService.hdel(ServerConstant.CHANNEL_MAP.getBytes(), channelId.getBytes());
            redisService.hdel(ServerConstant.CHANNEL_UID_MAP, tcpServerBean.getUid());
        }
    }

    public TcpServerBean getTcpServerBeanByChannelId(String channelId) {
        List<byte[]> list = redisService.hmget(ServerConstant.CHANNEL_MAP.getBytes(), channelId.getBytes());
        if (!CollectionUtils.isEmpty(list)) {
            return SerializationUtils.deserialize(list.get(0));
        }
        return null;
    }

    public Channel getChannelByChannelId(String channelId) {
        TcpServerBean bean = this.getTcpServerBeanByChannelId(channelId);
        if (bean != null) {
            return bean.getChannel();
        }
        return null;
    }

    public void removeChannelByChannelId(String channelId) {
        redisService.hdel(ServerConstant.CHANNEL_MAP.getBytes(), channelId.getBytes());
    }

    public TcpServerBean getTcpServerBeanByUid(String uid) {
        List<String> list = redisService.hmget(ServerConstant.CHANNEL_UID_MAP, uid);
        if (!CollectionUtils.isEmpty(list)) {
            byte[] bytes = list.get(0).getBytes();
            List<byte[]> list2 = redisService.hmget(ServerConstant.CHANNEL_MAP.getBytes(), bytes);
            if (!CollectionUtils.isEmpty(list)) {
                return SerializationUtils.deserialize(list2.get(0));
            }
        }
        return null;
    }

    public Channel getChannelByUid(String uid) {
        TcpServerBean bean = this.getTcpServerBeanByUid(uid);
        if (bean != null) {
            return bean.getChannel();
        }
        return null;
    }

    public void removeChannelByUid(String uid) {
        List<String> list = redisService.hmget(ServerConstant.CHANNEL_UID_MAP, uid);
        if (!CollectionUtils.isEmpty(list)) {
            byte[] channelId = list.get(0).getBytes();
            redisService.hdel(ServerConstant.CHANNEL_MAP.getBytes(), channelId);
        }
    }

    public int sendMsgByUid(String uid, String msg) {
        Channel channel = this.getChannelByUid(uid);
        if (channel != null) {
            channel.writeAndFlush(msg);
            LOGGER.info("发送消息成功，uid：{}, msg: {}", uid, msg);
            return 1;
        } else {
            LOGGER.error("连接channel为空，uid：{}, msg: {}", uid, msg);
            return -1;
        }
    }


    /**
     * 获取客户端ip
     * @param channel
     * @return
     */
    public String getHost(Channel channel) {
        String host = null;
        if (channel != null) {
            try {
                SocketAddress socketAddress = channel.remoteAddress();
                if (socketAddress instanceof InetSocketAddress) {
                    InetSocketAddress inetSocketAddress = (InetSocketAddress) socketAddress;
                    InetAddress address = inetSocketAddress.getAddress();
                    host = address.getHostAddress();
                }
            } catch (Exception e) {
                LOGGER.error("获取客户端ip出错", e);
            }
        }
        return host;
    }

    /**
     * 获取channelId
     * @param channel
     * @return
     */
    public String getChannelId(Channel channel) {
        String id = null;
        if (channel != null) {
            try {
                id = channel.id().asShortText();
            } catch (Exception e) {
                LOGGER.error("获取ChannelId出错", e);
            }
        }
        return id;
    }
}
