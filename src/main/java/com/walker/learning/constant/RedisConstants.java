package com.walker.learning.constant;

/**
 * RedisConstants
 *
 * @author walker lee
 * @date 2019/4/8
 */
public class RedisConstants {

    private static final String TOKEN_KEY_PREFIX = "token:%s";


    public static String getTokenKey(String token) {
        return String.format(TOKEN_KEY_PREFIX, token);
    }
}
