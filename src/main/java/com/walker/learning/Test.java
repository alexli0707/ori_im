package com.walker.learning;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test
 *
 * @author walker lee
 * @date 2019/1/16
 */
public class Test {
    public static void main(String[] args) {
//        ByteBuffer pingBuffer = ByteBuffer.allocate(1024);
//        String content = "这是a一段测试12语句!";
//        byte[] contentBytes =  content.getBytes();
//        pingBuffer.putInt(contentBytes.length);
//        System.out.println(pingBuffer.position());
//        pingBuffer.put(contentBytes);
//        System.out.println(pingBuffer.position());
//        pingBuffer.flip();
//        int length = pingBuffer.getInt();
//        System.out.println(length);
//        byte[] readContentBytes = new byte[length];
//        pingBuffer.get(readContentBytes);
//        String contentRead = new String(readContentBytes);
//        System.out.println(contentRead);

        Logger logger = LoggerFactory.getLogger("chapters.introduction.HelloWorld2");
        logger.debug("Hello world.");
        // print internal state
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        StatusPrinter.print(lc);
    }
}
