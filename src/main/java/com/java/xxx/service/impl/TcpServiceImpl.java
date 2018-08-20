package com.java.xxx.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.java.xxx.client.HttpUtils;
import com.java.xxx.constant.TcpConstant;
import com.java.xxx.service.TcpService;
import com.java.xxx.vo.LockReturn;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class TcpServiceImpl implements TcpService {

    private static final Logger logger = LoggerFactory.getLogger(TcpServiceImpl.class);

    private final static Map<String, OutputStream> outputStreamMap = TcpConstant.outputStreamMap;

    private static final String localhost = "http://127.0.0.1:8080/mobile";
    private static final String lock = "/lock";
    private static final String unLock = "/unLock";

    @Override
    public String close(Map<String, String> params) {
        try {
            String closeUid = params.get(TcpConstant.CLOSE_UID);
            if (check(closeUid)) {
                String ret = readData(lock, params);
                return ret;
            } else {
                return TcpConstant.ERROR;
            }
        } catch (Exception e) {
            logger.error(params.toString(), e);
            return TcpConstant.ERROR;
        }
    }

    @Override
    public String open(Map<String, String> params) {
        String openUid = params.get(TcpConstant.OPEN_UID);
        try {
            OutputStream outputStream = outputStreamMap.get(openUid);
            if (outputStream != null) {
                if (check(openUid)) {
                    String ret = JSONObject.toJSONString(new LockReturn(openUid, TcpConstant.OPEN, TcpConstant.OK));
                    logger.info("向设备响应：[{}]", ret);
                    outputStream.write(ret.getBytes());
                    return TcpConstant.OK;
                } else {
                    return TcpConstant.ERROR;
                }
            } else {
                logger.info("设备未在线[{}]", openUid);
                return TcpConstant.NOT_FIND;
            }
        } catch (Exception e) {
            logger.error(params.toString(), e);
            return TcpConstant.ERROR;
        }
    }

    private boolean check(String openUid) {
        return true;
    }

    String readData(String path, Map<String, String> params) {
        InputStream inputStream = null;
        try {
            HttpResponse httpResponse = HttpUtils.doGet(localhost, path, null, null, params);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (200 == statusCode) {
                HttpEntity entity = httpResponse.getEntity();
                inputStream = entity.getContent();
                byte[] data = new byte[2048];
                int length = inputStream.read(data);
                String ret = TcpConstant.OK;
                if (length != -1) {
                    ret = new String(data, 1, length-2);
                }
                return ret;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return TcpConstant.ERROR;
    }
}
