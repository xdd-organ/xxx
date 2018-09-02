package com.java.mbkh.server.bean;

import io.netty.channel.ChannelId;

import java.io.Serializable;

public class TcpServerBean implements Serializable {

    private static final long serialVersionUID = -4324993545779213218L;

    private String id;//channelId
    private String host;//客户端ip
    private ChannelId channelId;//
    private String uid;//设备uid

    public TcpServerBean(String id, String host, ChannelId channelId) {
        this.id = id;
        this.host = host;
        this.channelId = channelId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public ChannelId getChannelId() {
        return channelId;
    }

    public void setChannelId(ChannelId channelId) {
        this.channelId = channelId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
