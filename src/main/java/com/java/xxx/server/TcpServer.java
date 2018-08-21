package com.java.xxx.server;

import com.alibaba.fastjson.JSONObject;
import com.java.xxx.constant.TcpConstant;
import com.java.xxx.service.TcpService;
import com.java.xxx.service.impl.TcpServiceImpl;
import com.java.xxx.vo.LockReturn;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class TcpServer implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(TcpServer.class);

    private final static Map<String, OutputStream> outputStreamMap = TcpConstant.outputStreamMap;
    private final static Map<String, OutputStream> outputStreamMap2 = TcpConstant.outputStreamMap2;

    private static final TcpService service = new TcpServiceImpl();

    private Socket socket;

    public TcpServer(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        String uid = null;
        String hostAddress = null;
        logger.info("已连接[{}]个客户端，连接编号[{}]", outputStreamMap.size(), outputStreamMap.keySet().toString());
        logger.info("2已连接[{}]个客户端，连接编号[{}]", outputStreamMap2.size(), outputStreamMap2.keySet().toString());
        try (OutputStream outputStream = socket.getOutputStream();//获取客户端的OutputStream与inputStream
             InputStream inputStream = socket.getInputStream()) {
            hostAddress = socket.getInetAddress().getHostAddress();
            logger.info("主机[{}]，已连接", hostAddress);
            outputStreamMap2.put(hostAddress, outputStream);
            while (true) {
                //获得客户端的ip地址和主机名
                //读取数据
                byte[] data = new byte[1024];
                int length = inputStream.read(data);
                if (length != -1) {
                    String paramsJson = new String(data, 0, length);
                    logger.info("设备[{}:{}]，接收数据：{}", hostAddress, uid, paramsJson);
                    Map<String, String> params;
                    try {
                        params = JSONObject.parseObject(paramsJson, Map.class);
                    } catch (Exception ex) {
                        logger.error("解析请求参数异常");
                        params = new HashMap<>();
                    }
                    String type = params.get(TcpConstant.TYPE);
                    uid = params.get(TcpConstant.UID);

                    LockReturn lockReturn = new LockReturn(uid, type, TcpConstant.OK);
                    outputStreamMap.put(uid, outputStream);
                    if (TcpConstant.REGISTER.equals(type)) { //发送设备编号，维护设备编号与会话关系

                    } else if (TcpConstant.CLOSE.equals(type)) { //关锁
                        //关锁的设备编号
                        String close = service.close(params);
                        lockReturn.setRET(close);
                    } else if (TcpConstant.OPEN.equals(type)) { //关锁
                        if (TcpConstant.ADMIN_UID.equals(uid)) {
                            //解锁的设备编号
                            String open = service.open(params);
                            lockReturn.setRET(open);
                        } else {
                            String open = service.status(params);
                            continue;
                        }
                    } else if (TcpConstant.PING.equals(type)) { // ping

                    }  else if (TcpConstant.STATUS.equals(type)) { // ping
                        String open = service.status(params);
                        lockReturn.setRET(open);
                    } else if (TcpConstant.EXIT.equals(type)) { //关闭连接
                        String ret = JSONObject.toJSONString(lockReturn);
                        outputStream.write(ret.getBytes());
                        logger.info("设备[{}:{}]，返回数据：{}", hostAddress, uid, TcpConstant.OK);
                        break;
                    } else {
                        lockReturn.setRET(TcpConstant.ERROR);
                    }
                    String ret = JSONObject.toJSONString(lockReturn);
                    logger.info("设备[{}:{}]，返回数据：{}", hostAddress, uid, ret);
                    outputStream.write(ret.getBytes());
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("设备[" + hostAddress + ":" + uid + "]连接异常：" + e.getMessage(), e);
        } finally {
            if (StringUtils.isNotBlank(uid))outputStreamMap.remove(uid);
            if (StringUtils.isNotBlank(hostAddress)) outputStreamMap2.remove(hostAddress);
            logger.info("设备[{}:{}]，释放资源", hostAddress, uid);
            //关闭资源
            try {
                socket.close();
                logger.info("设备[{}:{}]，关闭连接", hostAddress, uid);
            } catch (Exception e) {
                logger.error("设备[" + hostAddress + ":" + uid + "]连接异常：" + e.getMessage(), e);
            }
        }
    }
}
