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
    private String content;


    public BaseImMsg(int cmdType, int senderId, int receiverId, String content) {
        this.cmdType = cmdType;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
    }


    public int getCmdType() {
        return cmdType;
    }

    public void setCmdType(int cmdType) {
        this.cmdType = cmdType;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    @Override
    public String toString() {
        return String.format("cmd:%d,senderId:%d,receiverId:%d,content:%s", cmdType, senderId, receiverId, content);
    }
}
