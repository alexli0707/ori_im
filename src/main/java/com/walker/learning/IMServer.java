package com.walker.learning;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.walker.learning.im.SelectionKeyHandler;
import com.walker.learning.im.ServerSelectionKeyHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

/**
 * IMServer
 *
 * @author walker lee
 * @date 2019/1/2
 */
public class IMServer {
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();


    public static void main(String[] args) {
        SelectionKeyHandler handler = new ServerSelectionKeyHandler();
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


}
