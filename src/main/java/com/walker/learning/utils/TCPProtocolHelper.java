package com.walker.learning.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.walker.learning.IMServer;
import com.walker.learning.constant.RedisConstants;
import com.walker.learning.constant.protocol.MsgType;
import com.walker.learning.im.ClientMsgBuilder;
import com.walker.learning.im.ServerMsgBuilder;
import com.walker.learning.models.AuthTokenMsgContent;
import com.walker.learning.models.BaseImMsg;
import com.walker.learning.models.BaseMsgContent;
import com.walker.learning.models.TokenMsgContent;
import com.walker.learning.store.UserDao;
import com.walker.learning.store.core.RedisConnInstance;
import com.walker.learning.store.model.User;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;


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
                LoggerHelper.getLogger(TCPProtocolHelper.class).info(String.format("receive pong from server:{%d}", senderId));
                break;
            case MsgType.SEND_MSG:
                BaseMsgContent msgContent = GSON.fromJson(content, BaseMsgContent.class);
                LoggerHelper.getLogger(TCPProtocolHelper.class).info(String.format("receive msg from client:{%d},content:{%s}", senderId, msgContent.toJsonStr()));
                break;
            case MsgType.TOKEN:
                TokenMsgContent tokenMsgContent = GSON.fromJson(content, TokenMsgContent.class);
                ClientMsgBuilder.setToken(tokenMsgContent.token);
                ClientMsgBuilder.setClientId(tokenMsgContent.id);
                LoggerHelper.getLogger(TCPProtocolHelper.class).info(String.format("receive msg from client:{%d},content:{%s}", senderId, tokenMsgContent));
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
        String token = imMsg.getToken();
        String content = imMsg.getContent();
        switch (cmdType) {
            case MsgType.PING:
                LoggerHelper.getLogger(TCPProtocolHelper.class).info(String.format("receive ping from clientId:{%d}", senderId));
                if (!UserDao.validToken(senderId, token)) {
                    SocketManager.getInstance().closeAndRemoveSocket(socketChannel);
                    socketChannel.close();
                    break;
                }
                ByteBuffer pongBuffer = ServerMsgBuilder.makePongMsg(senderId);
//                    byte[] bytes = new byte[pongBuffer.remaining()];
//                    pongBuffer.get(bytes);
                try {
                    socketChannel.write(pongBuffer);
                    LoggerHelper.getLogger(TCPProtocolHelper.class).info(String.format("send pong to clientId:{%d}", senderId));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case MsgType.AUTH:
                LoggerHelper.getLogger(TCPProtocolHelper.class).info(String.format("auth content is : %s", content));
                try {
                    AuthTokenMsgContent authTokenMsgContent = GSON.fromJson(content, AuthTokenMsgContent.class);
                    // 校验账户
                    boolean isValid = UserDao.valid(authTokenMsgContent);
                    if (isValid) {
                        //查询用户信息
                        User user = UserDao.getByUsername(authTokenMsgContent.username);
                        if (null == user) {
                            SocketManager.getInstance().closeAndRemoveSocket(socketChannel);
                            LoggerHelper.getLogger(TCPProtocolHelper.class).info("can not find username");
                        } else {
                            // 生成token
                            String uuid = ToolsUtil.generateUUID();
                            //存入缓存
                            RedisConnInstance.getInstance().set(RedisConstants.getTokenKey(uuid), GSON.toJson(user, User.class));
                            int clientId = user.getId();
                            SocketManager.getInstance().switchSocketToLabeledMap(clientId, socketChannel);
                            // 返回 token给客户端
                            LoggerHelper.getLogger(TCPProtocolHelper.class).info(String.format("authed client id is %d", clientId));
                            //返回token
                            ByteBuffer tokenBuffer = ServerMsgBuilder.makeTokenMsg(uuid, clientId);
                            socketChannel.write(tokenBuffer);
                        }
                    } else {
                        //关闭并移除连接
                        SocketManager.getInstance().closeAndRemoveSocket(socketChannel);
                        LoggerHelper.getLogger(TCPProtocolHelper.class).info("auth fail,close socket");
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MsgType.SEND_MSG:
                LoggerHelper.getLogger(TCPProtocolHelper.class).info("in receive MsgType.SEND_MSG");
                if (!UserDao.validToken(senderId, token)) {
                    SocketManager.getInstance().closeAndRemoveSocket(socketChannel);
                    socketChannel.close();
                    break;
                }
//                BaseMsgContent msgContent = GSON.fromJson(content, BaseMsgContent.class);
                // 判断接收消息方是否在线,如果在的话就直接投递消息
                SocketChannel sc = SocketManager.getInstance().getSocketByClientId(receiverId);
                if (null != sc) {
                    LoggerHelper.getLogger(TCPProtocolHelper.class).info(String.format("write msg from client %d,to client %d", senderId, receiverId));
                    sc.write(ServerMsgBuilder.makeContentMsg(senderId, receiverId, content));
                } else {
                    //当做离线或者推送消息
                }


                break;
            default:
                LoggerHelper.getLogger(TCPProtocolHelper.class).warn(String.format("not handled cmd:%d", cmdType));
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
        while (readBytes < ServerMsgBuilder.MSG_CONTENT_BASE_LENGTH) {
            int newReadBytes = socketChannel.read(readBuffer);
            readBytes += newReadBytes;
        }
        readBuffer.position(60);
        int length = readBuffer.getInt();
        int msgTotalLength = ServerMsgBuilder.MSG_CONTENT_BASE_LENGTH + length;
        int restContent = msgTotalLength - readBytes;
        while (restContent > 0) {
            readBuffer.position(readBytes);
            int newReadBytes = socketChannel.read(readBuffer);
            readBytes += newReadBytes;
            restContent = msgTotalLength - readBytes;
        }
        readBuffer.position(0);
        int cmdType = readBuffer.getInt();
        byte[] tokenBytes = new byte[ServerMsgBuilder.TOKEN_BYTES_LENGTH];
        readBuffer.get(tokenBytes);
        String token = new String(tokenBytes, StandardCharsets.UTF_8);
        int senderId = readBuffer.getInt();
        int receiverId = readBuffer.getInt();
        readBuffer.position(60);
        int contentLength = readBuffer.getInt();
        String content = "";
        if (contentLength > 0) {
            byte[] contentBytes = new byte[contentLength];
            readBuffer.get(contentBytes);
            content = new String(contentBytes, StandardCharsets.UTF_8);
        }
        BaseImMsg imMsg = new BaseImMsg(cmdType, senderId, receiverId, token, content);
        return imMsg;
    }

}
