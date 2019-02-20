package com.walker.learning;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.walker.learning.im.MsgBuilder;
import com.walker.learning.models.AuthMsgContent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static com.walker.learning.constant.protocol.MsgType.AUTH;
import static com.walker.learning.constant.protocol.MsgType.PING;
import static com.walker.learning.im.MsgBuilder.MSG_CONTENT_BASE_LENGTH;

/**
 * IMServer
 *
 * @author walker lee
 * @date 2019/1/2
 */
public class IMServer {
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();
    //线程安全
    private static List<SocketChannel> channels = Collections.synchronizedList(new ArrayList<SocketChannel>());

    public static void main(String[] args) {

        HandlerSelectionKey handler = new HandlerHandlerSelectionKeyImpl();

        try {
            //创建 ServerSocketChannel
            ServerSocketChannel server = ServerSocketChannel.open();
            server.configureBlocking(false);
            server.bind(new InetSocketAddress("localhost", 12345));
            //创建 Selector
            Selector selector = Selector.open();
            server.register(selector, SelectionKey.OP_ACCEPT);
            //死循环，持续接收 客户端连接
            while (true) {
                //selector.select(); 是阻塞方法
                int keys = selector.select();
                if (keys > 0) {
                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        it.remove();
                        //处理 SelectionKey
                        handler.handler(key, selector);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * SelectionKey 处理接口
     */
    public static interface HandlerSelectionKey {

        public void handler(SelectionKey key, Selector selector) throws IOException;

    }

    /**
     * SelectionKey 接口 实现类
     */
    public static class HandlerHandlerSelectionKeyImpl implements HandlerSelectionKey {

        @Override
        public void handler(SelectionKey key, Selector selector) throws IOException {
            int keyState = selectionKeyState(key);
            switch (keyState) {
                case SelectionKey.OP_ACCEPT:
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                    accept(serverSocketChannel, selector);
                    break;
                case SelectionKey.OP_READ:
                    SocketChannel readSocketChannel = (SocketChannel) key.channel();
                    read(readSocketChannel, selector);
                    break;
            }
        }

        /**
         * 获取 SelectionKey 是什么事件
         *
         * @param key
         * @return
         */
        private int selectionKeyState(SelectionKey key) {
            if (key.isAcceptable()) {
                return SelectionKey.OP_ACCEPT;
            } else if (key.isReadable()) {
                return SelectionKey.OP_READ;
            }
            return -1;
        }

        /**
         * 接口客户端请求
         *
         * @param serverSocketChannel
         * @param selector
         * @throws IOException
         */
        private void accept(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.socket().setKeepAlive(true);
            channels.add(socketChannel);
            //将 channel 注册到  Selector
            socketChannel.register(selector, SelectionKey.OP_READ);
        }

        /**
         * 读取客户端发送过来的信息
         *
         * @param socketChannel
         * @param selector
         * @throws IOException
         */
        private void read(SocketChannel socketChannel, Selector selector) throws IOException {
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
            handleReadBuffer(readBuffer);
        }

        private void handleReadBuffer(ByteBuffer byteBuffer) {
            byteBuffer.position(0);
            int cmdType = byteBuffer.getInt();
            int senderId = byteBuffer.getInt();
            int receiverId = byteBuffer.getInt();
            byteBuffer.position(28);
            int contentLength = byteBuffer.getInt();
            String content = "";
            if (contentLength > 0) {
                byte[] contentBytes = new byte[contentLength];
                byteBuffer.get(contentBytes);
                content = new String(contentBytes, StandardCharsets.UTF_8);
            }
            switch (cmdType) {
                case PING:
                    ByteBuffer pongBuffer = MsgBuilder.makePongMsg(senderId);
                    byte[] bytes = new byte[pongBuffer.remaining()];
                    pongBuffer.get(bytes);
                    break;
                case AUTH:
                    System.out.println(content);
                    try {
                        AuthMsgContent authMsgContent = GSON.fromJson(content, AuthMsgContent.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

            }
            System.out.printf("cmd:%d,senderId:%d,receiverId:%d,content:%s", cmdType, senderId, receiverId, content);

        }

        /**
         * 响应客户端请求
         *
         * @param socketChannel
         * @param msg
         * @throws IOException
         */
        private void write(SocketChannel socketChannel, String msg) throws IOException {
            msg = "游客" + socketChannel.hashCode() + "\r\n    " + msg;
            //响应消息
            byte[] responseByte = msg.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(responseByte.length);
            writeBuffer.put(responseByte);
            writeBuffer.flip();
            //响应客户端
            for (int i = 0; i < channels.size(); i++) {
                if (!socketChannel.equals(channels.get(i))) {
                    channels.get(i).write(writeBuffer);
                }
            }
        }

    }


}
