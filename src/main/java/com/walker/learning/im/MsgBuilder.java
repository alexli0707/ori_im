package com.walker.learning.im;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.walker.learning.constant.protocol.MsgType;
import com.walker.learning.models.AuthTokenMsgContent;
import com.walker.learning.models.TextMsg;

import java.nio.ByteBuffer;

/**
 * MsgBuilder
 * <p>
 * 消息体结构说明 cmd_type(4 bytes)| sender_id(4bytes) |receiver_id(4 bytes)|16位保留|content_length(4 bytes)|content
 * 消息头总共是32字节
 *
 * @author walker lee
 * @date 2019/1/16
 */
public class MsgBuilder implements MsgType {

    public static final int MSG_CONTENT_BASE_LENGTH = 32;
    private static final byte[] emptyBytes = new byte[16];

    private static final Gson GSON = new GsonBuilder().serializeNulls().create();


    private static ByteBuffer makeMsg(int cmdType, int senderId, int receiverId, String content) {
        if (content != null) {
            byte[] contentStrBytes = content.getBytes();
            int contentLength = contentStrBytes.length;
            int byteBufferSize = MSG_CONTENT_BASE_LENGTH + contentLength;
            ByteBuffer byteBuffer = ByteBuffer.allocate(byteBufferSize);
            byteBuffer.putInt(cmdType);
            byteBuffer.putInt(senderId);
            byteBuffer.putInt(receiverId);
            byteBuffer.put(emptyBytes);
            byteBuffer.putInt(contentLength);
            if (contentLength > 0) {
                byteBuffer.put(contentStrBytes);
            }
            byteBuffer.flip();
            return byteBuffer;
        } else {
            ByteBuffer byteBuffer = ByteBuffer.allocate(MSG_CONTENT_BASE_LENGTH);
            byteBuffer.putInt(cmdType);
            byteBuffer.putInt(senderId);
            byteBuffer.putInt(receiverId);
            byteBuffer.put(emptyBytes);
            byteBuffer.putInt(0);
            byteBuffer.flip();
            return byteBuffer;
        }
    }


    /**
     * 封装ping 消息
     *
     * @return
     */
    public static ByteBuffer makePingMsg(int senderId) {
        return MsgBuilder.makeMsg(PING, senderId, 0, null);
    }

    /**
     * 封装pong 消息
     *
     * @return
     */
    public static ByteBuffer makePongMsg(int receiver) {
        return MsgBuilder.makeMsg(PONG, 0, receiver, null);
    }

    /**
     * 封装auth 消息
     *
     * @return
     */
    public static ByteBuffer makeAuthMsg(int senderId, String token) {
        AuthTokenMsgContent authTokenMsgContent = new AuthTokenMsgContent(token);
        String authStr = GSON.toJson(authTokenMsgContent);
        return MsgBuilder.makeMsg(AUTH, senderId, 0, authStr);
    }


    public static ByteBuffer makeTextMsg(int senderId, int receiverId, String content) {
        TextMsg textMsg = new TextMsg(content);
        String textMsgStr = textMsg.toJsonStr();
        return MsgBuilder.makeMsg(SEND_MSG, senderId, receiverId, textMsgStr);
    }

    /**
     * 封装消息
     * @param senderId
     * @param receiverId
     * @param content
     * @return
     */
    public static ByteBuffer makeContentMsg(int senderId, int receiverId, String content) {
        return MsgBuilder.makeMsg(SEND_MSG, senderId, receiverId, content);
    }
}
