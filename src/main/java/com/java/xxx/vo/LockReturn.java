package com.java.xxx.vo;

/**
 * @author xdd
 * @date 2018/8/20
 */
public class LockReturn {
    private String UID;
    private String TYPE;
    private String RET;
    public LockReturn(String uid, String type, String ret) {
        this.UID = uid;
        this.TYPE = type;
        this.RET = ret;
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    public String getRET() {
        return RET;
    }

    public void setRET(String RET) {
        this.RET = RET;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
