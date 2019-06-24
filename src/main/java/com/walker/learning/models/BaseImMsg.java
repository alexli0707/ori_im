package com.walker.learning.models;

/**
 * BaseImMsg
 *
 * @author walker lee
 * @date 2019/2/27
 */
public class BaseImMsg {
    private int cmdType;
    private int senderId;
    private int receiverId;
    private String token;
    private String content;


    public BaseImMsg(int cmdType, int senderId, int receiverId, String token, String content) {
        this.cmdType = cmdType;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.token = token;
        this.content = content;
    }


    public int getCmdType() {
        return cmdType;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
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
        return String.format("cmd:%d,senderId:%d,receiverId:%d,content:%s,token:%s", cmdType, senderId, receiverId, content, token);
    }
}
