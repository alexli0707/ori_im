package com.walker.learning.models;

/**
 * BaseImMsg
 *
 * @author walker lee
 * @date 2019/2/27
 */
public class BaseImMsg {
    private int cmdType;
    private double senderId;
    private double receiverId;
    private String token;
    private String content;


    public BaseImMsg(int cmdType, double senderId, double receiverId, String token, String content) {
        this.cmdType = cmdType;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.token = token;
        this.content = content;
    }


    public int getCmdType() {
        return cmdType;
    }

    public double getSenderId() {
        return senderId;
    }

    public double getReceiverId() {
        return receiverId;
    }

    public String getToken() {
        return token;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return String.format("cmd:%s,senderId:%s,receiverId:%s,content:%s,token:%s", cmdType, senderId, receiverId, content, token);
    }
}
