package com.java.xxx;

import com.java.xxx.server.TcpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ServerSocket;
import java.net.Socket;

public class MainApplication {

    private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);

    public static void main(String[] args) {
        try (ServerSocket tcpServer = new ServerSocket(8090)) {//建立TCP连接服务,绑定端口
            //接受连接,每个TCP连接都是一个java线程
            logger.info("TCP服务启动成功");
            while (true) {
                Socket socket = tcpServer.accept();
                logger.info("有新客户端连接");
                new Thread(new TcpServer(socket)).start();
            }
        } catch (Exception e) {
            logger.error("TCP服务运行异常，关闭TCP服务", e);
        }
    }

}
