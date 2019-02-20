package com.walker.learning;

import com.walker.learning.im.MsgBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * IMClient
 *
 * @author walker lee
 * @date 2019/1/2
 */


public class IMClient {
    private static final int sender_id = 1;

    public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
        InetSocketAddress socketAddress = new InetSocketAddress("localhost", 12345);
        SocketChannel socketChannel = SocketChannel.open(socketAddress);
        socketChannel.socket().setKeepAlive(true);
        Socket socket = socketChannel.socket();
        if (!socket.isConnected()) {
            //重试方案
            System.out.println("socket is not connected");
            return;
        } else {
            System.out.println("socket is connected");
        }
        OutputStream outStream = socket.getOutputStream();
        ByteBuffer authBuffer = MsgBuilder.makeAuthMsg(sender_id, "admin", "admin");
        byte[] bytes = new byte[authBuffer.remaining()];
        authBuffer.get(bytes);
//        ByteBuffer pingBuffer = MsgBuilder.makePingMsg(sender_id);
//        byte[] bytes = new byte[pingBuffer.remaining()];
//        pingBuffer.get(bytes);
        outStream.write(bytes);
        outStream.flush();
        socket.close();


//        pingBuffer.put();
//        socket.getOutputStream();
//        Thread inT = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    while(true) {
//                        InputStream inputStream = socket.getInputStream();
//                        byte[] b = new byte[8192];
//                        int readSize = inputStream.read(b);
//                        System.out.println(new String(b,0,readSize));
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        inT.start();
//        while(true) {
//            InputStreamReader stdin = new InputStreamReader(System.in);//键盘输入
//            BufferedReader bufin = new BufferedReader(stdin);
//            String str = bufin.readLine();
//            System.out.println(str);
//
//            OutputStream outStream = socket.getOutputStream();
//            outStream.write(str.getBytes());
//            outStream.flush();
//        }


    }

}
