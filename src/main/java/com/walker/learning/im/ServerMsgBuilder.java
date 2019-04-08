package com.walker.learning.im;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.walker.learning.constant.protocol.MsgType;
import com.walker.learning.models.TokenMsgContent;
import com.walker.learning.utils.IMExceptionHeleper;
import com.walker.learning.utils.ToolsUtil;

import java.nio.ByteBuffer;

/**
 * MsgBuilder
 * <p>
 * 消息体结构说明 cmd_type(4 bytes)| sender_id(4bytes) |receiver_id(4 bytes)|16位保留|content_length(4 bytes)|content
 * 消息头总共是32字节
 * <p>
 * 消息结构:消息头 + 消息体
 * 消息头结构:  cmd_type(4 bytes:int)|sender_token(32bytes) |sender_id(8 bytes:double)  |receiver_id(8bytes:double) |16位保留|content_length(4 bytes)
 * 消息体: content
 *
 * @author walker lee
 * @date 2019/1/16
 */
public class ServerMsgBuilder implements MsgType {

    public static final int MSG_CONTENT_BASE_LENGTH = 72;
    public static final double DEFALUT_SERVER_ID = 0;
    private static final byte[] emptyBytes = new byte[16];
    public static final int TOKEN_BYTES_LENGTH = 32;
    private static final String DEFAULT_SERVER_TOKEN = ToolsUtil.generateUUID();
    //客户端默认token,仅供鉴权判断使用
    public static final String DEFAULT_CLIENT_TOKEN = ToolsUtil.generateUUID();

    private static final Gson GSON = new GsonBuilder().serializeNulls().create();


    private static ByteBuffer makeMsg(int cmdType, String senderToken, double senderId, double receiverId, String content) {
        if (content != null) {
            byte[] contentStrBytes = content.getBytes();
            byte[] tokenBytes = senderToken.getBytes();
            if (tokenBytes.length != TOKEN_BYTES_LENGTH) {
                throw IMExceptionHeleper.ILLEGAL_TOKEN_LENGTH_EXCEPTION;
            }
            int contentLength = contentStrBytes.length;
            int byteBufferSize = MSG_CONTENT_BASE_LENGTH + contentLength;
            ByteBuffer byteBuffer = ByteBuffer.allocate(byteBufferSize);
            byteBuffer.putInt(cmdType);
            byteBuffer.put(tokenBytes);
            byteBuffer.putDouble(senderId);
            byteBuffer.putDouble(receiverId);
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
            byteBuffer.putDouble(senderId);
            byteBuffer.putDouble(receiverId);
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
    public static ByteBuffer makePingMsg(String token, double senderId) {
        return ServerMsgBuilder.makeMsg(PING, token, senderId, 0, null);
    }

    /**
     * 封装pong 消息
     *
     * @return
     */
    public static ByteBuffer makePongMsg(double receiver) {
        return ServerMsgBuilder.makeMsg(PONG, DEFAULT_SERVER_TOKEN, 0, receiver, null);
    }

    /**
     * 封装token返回消息
     *
     * @return
     */
    public static ByteBuffer makeTokenMsg(String token, double receiverId) {
        TokenMsgContent tokenMsgContent = new TokenMsgContent(receiverId, token);
        String tokenStr = GSON.toJson(tokenMsgContent);
        return ServerMsgBuilder.makeMsg(TOKEN, DEFAULT_SERVER_TOKEN, DEFALUT_SERVER_ID, receiverId, tokenStr);
    }


    /**
     * 封装消息
     *
     * @param senderId
     * @param receiverId
     * @param content
     * @return
     */
    public static ByteBuffer makeContentMsg(double senderId, double receiverId, String content) {
        return ServerMsgBuilder.makeMsg(SEND_MSG, DEFAULT_SERVER_TOKEN, senderId, receiverId, content);
    }
}
