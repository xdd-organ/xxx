package com.java.xxx.constant;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public final class TcpConstant {
    public static final String TYPE = "TYPE";
    public static final String OPEN = "OPEN";
    public static final String REGISTER = "REGISTER";
    public static final String UID = "UID";
    public static final String OK = "OK";
    public static final String ERROR = "ERROR";
    public static final String CLOSE = "CLOSE";
    public static final String PING = "PING";
    public static final String EXIT = "EXIT";
    public static final String OPEN_UID = "OPEN_UID";
    public static final String CLOSE_UID = "CLOSE_UID";
    public static final String NOT_FIND = "NOT_FIND";
    public static final String STATUS = "STATUS";
    public static final String ADMIN_UID = "123456789";

    public final static Map<String, OutputStream> outputStreamMap = new HashMap<>();
    public final static Map<String, OutputStream> outputStreamMap2 = new HashMap<>();
}
