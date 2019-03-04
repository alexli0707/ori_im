package com.walker.learning.im;

import com.walker.learning.utils.SocketManager;
import com.walker.learning.utils.TCPProtocolHelper;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * ServerSelectionKeyHandler
 *
 * @author walker lee
 * @date 2019/2/27
 */
public class ServerSelectionKeyHandler extends SelectionKeyHandler {
    @Override
    public void handler(SelectionKey key, Selector selector) {
        int keyState = selectionKeyState(key);
        switch (keyState) {
            case SelectionKey.OP_ACCEPT:
                ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                try {
                    accept(serverSocketChannel, selector);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case SelectionKey.OP_READ:
                SocketChannel readSocketChannel = (SocketChannel) key.channel();
                try {
                    read(readSocketChannel, selector);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
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
        //将 channel 注册到  Selector
        socketChannel.register(selector, SelectionKey.OP_READ);
        SocketManager.getInstance().putUnlabeledSocket(socketChannel);
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
