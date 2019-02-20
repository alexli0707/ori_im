package com.walker.learning.im;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.walker.learning.constant.protocol.MsgType;
import com.walker.learning.models.AuthMsgContent;

import java.nio.ByteBuffer;

/**
 * MsgBuilder
 * <p>
 * 消息体结构说明 cmd_type(4 bytes)| sender_id(4bytes) |receiver_id(4 bytes)|16位保留|content_length(4 bytes)|content
 *
 * @author walker lee
 * @date 2019/1/16
 */
public class MsgBuilder implements MsgType {

    public static final int MSG_CONTENT_BASE_LENGTH = 32;

    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    /**
     * 封装ping 消息
     *
     * @return
     */
    public static ByteBuffer makePingMsg(int senderId) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
        byteBuffer.putInt(PING);
        byteBuffer.putInt(senderId);
        byteBuffer.putInt(0);         //receiver_id
        byteBuffer.put(new byte[16]); //预占位
        byteBuffer.putInt(0);    //content_length
        byteBuffer.flip();
        return byteBuffer;
    }

    /**
     * 封装pong 消息
     *
     * @return
     */
    public static ByteBuffer makePongMsg(int receiverId) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(256);
        byteBuffer.putInt(PONG);
        byteBuffer.putInt(0);
        byteBuffer.putInt(receiverId);         //receiver_id
        byteBuffer.put(new byte[16]); //预占位
        byteBuffer.putInt(0);    //content_length
        byteBuffer.flip();
        return byteBuffer;
    }

    /**
     * 封装auth 消息
     *
     * @return
     */
    public static ByteBuffer makeAuthMsg(int senderId, String uername, String password) {
        AuthMsgContent authMsgContent = new AuthMsgContent(uername, password);
        String authStr = GSON.toJson(authMsgContent);
        byte[] authStrBytes = authStr.getBytes();
        System.out.println(authStrBytes.length);
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        byteBuffer.putInt(AUTH);
        byteBuffer.putInt(senderId);
        byteBuffer.putInt(0);         //receiver_id
        byteBuffer.put(new byte[16]); //预占位
        byteBuffer.putInt(authStrBytes.length);    //content_length
        byteBuffer.put(authStrBytes);
        byteBuffer.flip();
        return byteBuffer;
    }
}
