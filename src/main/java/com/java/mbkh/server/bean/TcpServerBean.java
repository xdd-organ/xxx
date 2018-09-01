package com.java.mbkh.server.bean;

import io.netty.channel.Channel;

import java.io.Serializable;

public class TcpServerBean implements Serializable {

    private static final long serialVersionUID = -4324993545779213218L;

    private String id;//channelId
    private String host;//客户端ip
    private Channel Channel;//
    private String uid;//设备uid

    public TcpServerBean(String id, String host, io.netty.channel.Channel channel) {
        this.id = id;
        this.host = host;
        Channel = channel;
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

    public io.netty.channel.Channel getChannel() {
        return Channel;
    }

    public void setChannel(io.netty.channel.Channel channel) {
        Channel = channel;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
