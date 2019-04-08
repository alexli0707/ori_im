package com.walker.learning.im;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.walker.learning.constant.protocol.MsgType;
import com.walker.learning.models.AuthTokenMsgContent;
import com.walker.learning.models.TextMsg;
import com.walker.learning.utils.IMExceptionHeleper;
import com.walker.learning.utils.ToolsUtil;

import java.nio.ByteBuffer;

/**
 * MsgBuilder
 * 消息结构:消息头 + 消息体
 * 消息头结构:  cmd_type(4 bytes:int)|sender_token(32bytes) |sender_id(8 bytes:double)  |receiver_id(8bytes:double) |16位保留|content_length(4 bytes)
 * 消息体: content
 *
 * @author walker lee
 * @date 2019/1/16
 */
public class ClientMsgBuilder implements MsgType {
    private static final double DEFAULT_CLIENT_ID = -1;
    private static final double DEFAULT_SERVER_ID = 0;
    public static final int MSG_CONTENT_BASE_LENGTH = 72;
    private static final byte[] emptyBytes = new byte[16];
    private static final int TOKEN_BYTES_LENGTH = 32;
    //客户端默认token,仅供鉴权判断使用
    public static final String DEFAULT_CLIENT_TOKEN = ToolsUtil.generateUUID();

    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    private static double clientId;
    private static String token;


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
    public static ByteBuffer makePingMsg() {
        return ClientMsgBuilder.makeMsg(PING, ClientMsgBuilder.getToken(), ClientMsgBuilder.getClientId(), DEFAULT_SERVER_ID, null);
    }


    /**
     * 封装auth 消息
     *
     * @return
     */
    public static ByteBuffer makeAuthMsg(String username, String password) {
        AuthTokenMsgContent authTokenMsgContent = new AuthTokenMsgContent(username, password);
        String authStr = GSON.toJson(authTokenMsgContent);
        return ClientMsgBuilder.makeMsg(AUTH, DEFAULT_CLIENT_TOKEN, DEFAULT_CLIENT_ID, DEFAULT_SERVER_ID, authStr);
    }


    public static ByteBuffer makeTextMsg(double receiverId, String content) {
        TextMsg textMsg = new TextMsg(content);
        String textMsgStr = textMsg.toJsonStr();
        return ClientMsgBuilder.makeMsg(SEND_MSG, ClientMsgBuilder.getToken(), ClientMsgBuilder.getClientId(), receiverId, textMsgStr);
    }


    public static void setClientId(double clientId) {
        ClientMsgBuilder.clientId = clientId;
    }

    public static void setToken(String token) {
        ClientMsgBuilder.token = token;
    }

    public static double getClientId() {
        return clientId;
    }

    public static String getToken() {
        return token;
    }
}
