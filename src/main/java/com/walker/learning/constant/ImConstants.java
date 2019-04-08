package com.walker.learning.constant;

/**
 * ImConstants
 *
 * @author walker lee
 * @date 2019/2/22
 */
public class ImConstants {
    //  握手accept之后最晚接收客户端auth命令的间隔时间,超出该时间则当作无效socket,单位s
    public static final int ACCEPT_TO_AUTH_MAX_DURATION = 8;
    //  客户端ping pong间隔, 单位s
    public static final int PING_DURATION = 60;
}
