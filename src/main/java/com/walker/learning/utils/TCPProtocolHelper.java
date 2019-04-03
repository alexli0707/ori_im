package com.walker.learning.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.walker.learning.IMServer;
import com.walker.learning.constant.ForTestTokenPool;
import com.walker.learning.constant.protocol.MsgType;
import com.walker.learning.im.MsgBuilder;
import com.walker.learning.models.AuthTokenMsgContent;
import com.walker.learning.models.BaseImMsg;
import com.walker.learning.models.BaseMsgContent;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

import static com.walker.learning.im.MsgBuilder.MSG_CONTENT_BASE_LENGTH;

/**
 * TCPHelper
 *
 * @author walker lee
 * @date 2019/2/22
 */
public class TCPProtocolHelper {
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    public static void handleClientRead(SocketChannel socketChannel) throws IOException {
        BaseImMsg imMsg = TCPProtocolHelper.readInBuffer(socketChannel);
        if (null == imMsg) {
            LoggerHelper.getLogger(IMServer.class).error("readInBuffer return null IMmsg");
            return;
        }
        int cmdType = imMsg.getCmdType();
        int senderId = imMsg.getSenderId();
        int receiverId = imMsg.getReceiverId();
        String content = imMsg.getContent();
        switch (cmdType) {
            case MsgType.PONG:
                LoggerHelper.getLogger(TCPProtocolHelper.class).info(String.format("receive pong from server:{%s}", senderId));
                break;
            case MsgType.SEND_MSG:
                BaseMsgContent msgContent = GSON.fromJson(content, BaseMsgContent.class);
                LoggerHelper.getLogger(TCPProtocolHelper.class).info(String.format("receive msg from client:{%s},content:{%s}", senderId, msgContent.toJsonStr()));
                break;
            default:
                LoggerHelper.getLogger(TCPProtocolHelper.class).warn(String.format("not handled cmd:%s", cmdType));
                break;
        }
        LoggerHelper.getLogger(TCPProtocolHelper.class).info(imMsg.toString());
    }


    public static void handleServerRead(SocketChannel socketChannel) throws IOException {
        BaseImMsg imMsg = TCPProtocolHelper.readInBuffer(socketChannel);
        if (null == imMsg) {
            LoggerHelper.getLogger(TCPProtocolHelper.class).error("readInBuffer return null IMmsg");
            return;
        }
        int cmdType = imMsg.getCmdType();
        int senderId = imMsg.getSenderId();
        int receiverId = imMsg.getReceiverId();
        String content = imMsg.getContent();
        switch (cmdType) {
            case MsgType.PING:
                LoggerHelper.getLogger(TCPProtocolHelper.class).info(String.format("receive ping from clientId:{%s}", senderId));
                ByteBuffer pongBuffer = MsgBuilder.makePongMsg(senderId);
//                    byte[] bytes = new byte[pongBuffer.remaining()];
//                    pongBuffer.get(bytes);
                try {
                    socketChannel.write(pongBuffer);
                    LoggerHelper.getLogger(TCPProtocolHelper.class).info(String.format("send pong to clientId:{%s}", senderId));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case MsgType.AUTH:
                LoggerHelper.getLogger(TCPProtocolHelper.class).info(String.format("auth content is : %s", content));
                try {
                    AuthTokenMsgContent authTokenMsgContent = GSON.fromJson(content, AuthTokenMsgContent.class);
                    int clientId = ForTestTokenPool.getIdByToken(authTokenMsgContent.token);
                    SocketManager.getInstance().switchSocketToLabeledMap(clientId, socketChannel);
                    LoggerHelper.getLogger(TCPProtocolHelper.class).info(String.format("authed client id is %s", clientId));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MsgType.SEND_MSG:
//                BaseMsgContent msgContent = GSON.fromJson(content, BaseMsgContent.class);
                // 判断接收消息方是否在线,如果在的话就直接投递消息
                SocketChannel sc = SocketManager.getInstance().getSocketByClientId(receiverId);
                if (null != sc) {
                    LoggerHelper.getLogger(TCPProtocolHelper.class).info(String.format("write msg from client %s,to client %s", senderId, receiverId));
                    sc.write(MsgBuilder.makeContentMsg(senderId, receiverId, content));
                } else {
                    //当做离线或者推送消息
                }


                break;
            default:
                LoggerHelper.getLogger(TCPProtocolHelper.class).warn(String.format("not handled cmd:%s", cmdType));
                break;
        }
        LoggerHelper.getLogger(TCPProtocolHelper.class).info(imMsg.toString());
    }


    /**
     * 读取客户端发送过来的信息
     *
     * @param socketChannel
     * @throws IOException
     */
    public static BaseImMsg readInBuffer(SocketChannel socketChannel) throws IOException {
        ByteBuffer readBuffer = ByteBuffer.allocate(8192);
        int readBytes = socketChannel.read(readBuffer);
        while (readBytes < MSG_CONTENT_BASE_LENGTH) {
            int newReadBytes = socketChannel.read(readBuffer);
            readBytes += newReadBytes;
        }
        readBuffer.position(28);
        int length = readBuffer.getInt();
        int msgTotalLength = MSG_CONTENT_BASE_LENGTH + length;
        int restContent = msgTotalLength - readBytes;
        while (restContent > 0) {
            readBuffer.position(readBytes);
            int newReadBytes = socketChannel.read(readBuffer);
            readBytes += newReadBytes;
            restContent = msgTotalLength - readBytes;
        }
        readBuffer.position(0);
        int cmdType = readBuffer.getInt();
        int senderId = readBuffer.getInt();
        int receiverId = readBuffer.getInt();
        readBuffer.position(28);
        int contentLength = readBuffer.getInt();
        String content = "";
        if (contentLength > 0) {
            byte[] contentBytes = new byte[contentLength];
            readBuffer.get(contentBytes);
            content = new String(contentBytes, StandardCharsets.UTF_8);
        }
        BaseImMsg imMsg = new BaseImMsg(cmdType, senderId, receiverId, content);
        return imMsg;
    }

}
