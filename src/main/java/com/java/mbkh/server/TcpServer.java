package com.java.mbkh.server;

import com.java.mbkh.server.channel.EhcoServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author xdd
 * @date 2018/8/23
 */
@Service
public class TcpServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TcpServer.class);

    @Value("${port:8090}")
    private int port;

    @Autowired
    private EhcoServerInitializer ehcoServerInitializer;

    @PostConstruct
    public void init() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            LOGGER.info("server start port:{}", port);
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
            b.childHandler(ehcoServerInitializer);

            // 服务器绑定端口监听
            ChannelFuture f = b.bind(port).sync();
            // 监听服务器关闭监听
            f.channel().closeFuture().sync();

            LOGGER.info("###########################################");
            // 可以简写为
            /* b.bind(portNumber).sync().channel().closeFuture().sync(); */
        } catch (Exception e) {
            LOGGER.error("异常：" + e.getMessage(), e);
        } finally {
            LOGGER.info("server shutdown");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
