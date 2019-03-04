package com.walker.learning.im;

import com.walker.learning.constant.ForTestTokenPool;
import com.walker.learning.utils.LoggerHelper;
import com.walker.learning.utils.TCPProtocolHelper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * ClientSelectionKeyHandler
 *
 * @author walker lee
 * @date 2019/2/27
 */
public class ClientSelectionKeyHandler extends SelectionKeyHandler {
    private int senderId;

    public ClientSelectionKeyHandler(int senderId) {
        this.senderId = senderId;
    }

    @Override
    public void handler(SelectionKey key, Selector selector) {
        int keyState = selectionKeyState(key);
        switch (keyState) {
            case SelectionKey.OP_CONNECT:
                LoggerHelper.getLogger(ClientSelectionKeyHandler.class).info("in connect");
                SocketChannel channel = (SocketChannel) key.channel();
                if (null != channel && channel.isConnectionPending()) {
                    try {
                        channel.finishConnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                ByteBuffer authBuffer = MsgBuilder.makeAuthMsg(this.senderId, ForTestTokenPool.getTokenById(senderId));
                try {
                    channel.write(authBuffer);
                    channel.register(selector, SelectionKey.OP_READ);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case SelectionKey.OP_READ:
                LoggerHelper.getLogger(ClientSelectionKeyHandler.class).info("in read");
                SocketChannel readSocketChannel = (SocketChannel) key.channel();
                try {
                    read(readSocketChannel, selector);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case SelectionKey.OP_WRITE:
                LoggerHelper.getLogger(ClientSelectionKeyHandler.class).info("in write");
                break;
            default:
                LoggerHelper.getLogger(ClientSelectionKeyHandler.class).info(String.valueOf(keyState));
        }
    }


    /**
     * 处理客户端发送过来的信息
     *
     * @param socketChannel
     * @param selector
     * @throws IOException
     */
    private void read(SocketChannel socketChannel, Selector selector) throws IOException {
        TCPProtocolHelper.handleServerRead(socketChannel);
    }
}
