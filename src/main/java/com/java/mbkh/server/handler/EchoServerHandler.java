package com.java.mbkh.server.handler;

import com.alibaba.fastjson.JSONObject;
import com.java.common.jms.AdvancedGroupQueueSender;
import com.java.common.redis.service.RedisService;
import com.java.mbkh.server.bean.TcpServerBean;
import com.java.mbkh.server.constant.ServerConstant;
import com.java.mbkh.server.manager.ChannelManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.List;

@Service("echoServerHandler")
//@Scope("prototype")
//特别注意这个注解@Sharable，默认的4版本不能自动导入匹配的包，需要手动加入
//地址是import io.netty.channel.ChannelHandler.Sharable;
@Sharable
public class EchoServerHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EchoServerHandler.class);

    private ChannelGroup channels = ChannelManager.channels;

    @Resource(name = "msgSender")
    private AdvancedGroupQueueSender msgSender;
    @Autowired
    private ChannelManager channelManager;

    /**
     * 有新的连接加入
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {  // (2)
        Channel channel = ctx.channel();
        LOGGER.info("客户端[{}],连接[{}]加入", channelManager.getHost(channel), channelManager.getChannelId(channel));
        channelManager.addChannel(channel);
    }

    /**
     * 有连接关闭
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {  // (3)
        Channel channel = ctx.channel();
        LOGGER.info("客户端[{}],连接[{}]关闭", channelManager.getHost(channel), channelManager.getChannelId(channel));
        channelManager.removeChannel(channel);
    }

    /**
     * 读取到消息
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, final String msg) throws Exception {
        // 收到消息直接打印输出
        Channel channel = ctx.channel();
        final String channelId = channelManager.getChannelId(channel);
        LOGGER.info("客户端[{}],连接[{}]接收到消息：{}", channelManager.getHost(channel), channelId, msg);
        String sendMsg = JSONObject.toJSONString(new HashMap() {{
            put("msg", msg);
            put("channelId", channelId);
        }});
        msgSender.sendMsg(sendMsg, ServerConstant.DEFAULT);
    }
    
    /**
     * 建立新的连接  channelActive 方法 在channel被启用的时候触发 (在建立连接的时候)
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        LOGGER.info("客户端[{}],连接[{}]已连接", channelManager.getHost(channel), channelManager.getChannelId(channel));
        super.channelActive(ctx);
    }

    /**
     * 异常处理
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        LOGGER.error("客户端[{" + channelManager.getHost(channel) + "}],连接[" + channelManager.getChannelId(channel) + "]运行出现异常：" + cause.getMessage(), cause);
        super.exceptionCaught(ctx, cause);
    }

}