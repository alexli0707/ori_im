package com.walker.learning;

import com.walker.learning.constant.ImConstants;
import com.walker.learning.im.ClientMsgBuilder;
import com.walker.learning.im.ClientSelectionKeyHandler;
import com.walker.learning.im.SelectionKeyHandler;
import com.walker.learning.utils.LoggerHelper;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.*;

/**
 * IMClient
 *
 * @author walker lee
 * @date 2019/1/2
 */


public class IMClient2 {
    private static double clientId = -1;
    private static ScheduledExecutorService sPingExecutorService;
    private static ExecutorService sExecutorService;
    private static String username = "user1";
    private static String password = "123456";

    public static void main(String[] args) throws IOException, InterruptedException {
        final SelectionKeyHandler handler = new ClientSelectionKeyHandler(username, password);
        InetSocketAddress socketAddress = new InetSocketAddress("localhost", 12345);
        final SocketChannel socketChannel = SocketChannel.open(socketAddress);
        socketChannel.configureBlocking(false);
        final Selector selector = Selector.open();
        if (socketChannel.isConnected()) {
            LoggerHelper.getLogger(IMClient2.class).info("in connect");
            socketChannel.register(selector, SelectionKey.OP_READ);
            ByteBuffer authBuffer = ClientMsgBuilder.makeAuthMsg(username, password);
            try {
                socketChannel.write(authBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
        }
        sPingExecutorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("daemon-socket-ping-pool-%d").daemon(true).build());
        sPingExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    ByteBuffer pongBuffer = ClientMsgBuilder.makePingMsg();
                    socketChannel.write(pongBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 10, ImConstants.PING_DURATION, TimeUnit.SECONDS);
        sExecutorService = Executors.newSingleThreadExecutor();
        sExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    int keys = 0;
                    try {
                        keys = selector.select();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
            }
        });

        while (true) {
            InputStreamReader stdin = new InputStreamReader(System.in);//键盘输入
            BufferedReader bufin = new BufferedReader(stdin);
            String content = bufin.readLine();
            System.out.println(content);
            LoggerHelper.getLogger(IMClient2.class).info(String.format("input is : %s", content));
            ByteBuffer byteBuffer = ClientMsgBuilder.makeTextMsg(1, content);
            socketChannel.write(byteBuffer);
        }


    }

}
