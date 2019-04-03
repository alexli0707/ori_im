package com.walker.learning.utils;

import com.walker.learning.constant.ImConstants;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * SocketManager
 * <p>
 * <p>
 * 负责管理socket的生命,每8s做一次校验,关闭 CHANNEL_TO_TIMESTAMP_MAP中8s前建立的连接,这些连接是没有标识身份的未知连接
 *
 * @author walker lee
 * @date 2019/2/22
 */
public class SocketManager {


    //用户id -> channel 的字典.负责socket管理消息投递
    private static ConcurrentHashMap<Integer, SocketChannel> ID_TO_CHANNEL_MAP = new ConcurrentHashMap<Integer, SocketChannel>();

    //channel -> 连接时间的字段,负责管理未标识身份的连接,定时去清理关闭连接后5s内未标识身份的连接
    private static ConcurrentHashMap<SocketChannel, Integer> CHANNEL_TO_TIMESTAMP_MAP = new ConcurrentHashMap<SocketChannel, Integer>();


    private static SocketManager sSocketManager;
    private static Logger sLogger;
    private static ScheduledExecutorService sExecutorService;


    private SocketManager() {
        ID_TO_CHANNEL_MAP.clear();
        CHANNEL_TO_TIMESTAMP_MAP.clear();
        //org.apache.commons.lang3.concurrent.BasicThreadFactory
        sExecutorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("daemon-socket-schedule-pool-%d").daemon(true).build());
        sExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Iterator<Map.Entry<SocketChannel, Integer>> entries = CHANNEL_TO_TIMESTAMP_MAP.entrySet().iterator();
                int now = TimeUtils.getNowUnixTimeStamp();
                while (entries.hasNext()) {
                    Map.Entry<SocketChannel, Integer> entry = entries.next();
                    int addedTime = entry.getValue();
                    if ((now - addedTime) >= ImConstants.ACCEPT_TO_AUTH_MAX_DURATION) {
                        SocketChannel socketChannel = entry.getKey();
                        sLogger.info("remove Key = " + socketChannel);
                        CHANNEL_TO_TIMESTAMP_MAP.remove(socketChannel);
                        try {
                            socketChannel.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        }, 1, ImConstants.ACCEPT_TO_AUTH_MAX_DURATION, TimeUnit.SECONDS);
        sLogger = LoggerHelper.getLogger(SocketManager.class);
    }

    public static SocketManager getInstance() {
        if (null == sSocketManager) {
            synchronized (SocketManager.class) {
                if (null == sSocketManager) {
                    sSocketManager = new SocketManager();
                }
            }
        }
        return sSocketManager;
    }

    /**
     * socketChannel 放到 CHANNEL_TO_TIMESTAMP_MAP 中管理
     *
     * @param socketChannel
     */
    public void putUnlabeledSocket(SocketChannel socketChannel) {
        int now = TimeUtils.getNowUnixTimeStamp();
        CHANNEL_TO_TIMESTAMP_MAP.put(socketChannel, now);
        sLogger.info(String.format("putUnlabeledSocket: socketChannel{%s}", socketChannel));
        sLogger.info(String.format("CHANNEL_TO_TIMESTAMP_MAP size is : %s", CHANNEL_TO_TIMESTAMP_MAP.size()));
    }

    /**
     * 将校验过身份的socket放到 ID_TO_CHANNEL_MAP 中管理
     *
     * @param clientId
     * @param socketChannel
     */
    public void switchSocketToLabeledMap(int clientId, SocketChannel socketChannel) {
        CHANNEL_TO_TIMESTAMP_MAP.remove(socketChannel);
        ID_TO_CHANNEL_MAP.put(clientId, socketChannel);
        sLogger.info(String.format("switchSocketToLabeledMap: clientId{%s},socketChannel{%s}", clientId, socketChannel));
        sLogger.info(String.format("CHANNEL_TO_TIMESTAMP_MAP size is : %s", CHANNEL_TO_TIMESTAMP_MAP.size()));
        sLogger.info(String.format("ID_TO_CHANNEL_MAP size is : %s", ID_TO_CHANNEL_MAP.size()));

    }

    /**
     * 根据客户端id获取socket channel
     *
     * @param clientId
     */
    public SocketChannel getSocketByClientId(int clientId) {
        return ID_TO_CHANNEL_MAP.get(clientId);
    }


}
