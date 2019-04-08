package com.walker.learning.store.core;


import java.io.IOException;
import java.util.Properties;

/**
 * StoreConfig
 *
 * @author walker lee
 * @date 2019/4/4
 */
public class StoreConfig {
    private static final Properties sStoreProperties;
    public static final String IM_MYSQL_URL;
    public static final String IM_MYSQL_USERNAME;
    public static final String IM_MYSQL_PASSWORD;
    public static final String IM_REDIS_HOST;
    public static final int IM_REDIS_PORT;
    public static final String IM_REDIS_PASSWORD;
    public static final int IM_REDIS_TIMEOUT;
    public static final int IM_REDIS_DEFAULT_DB;


    static {
        sStoreProperties = new Properties();
        try {
            sStoreProperties.load(StoreConfig.class.getClassLoader().getResourceAsStream("store.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        IM_MYSQL_URL = sStoreProperties.getProperty("im_mysql_url");
        IM_MYSQL_USERNAME = sStoreProperties.getProperty("im_mysql_username");
        IM_MYSQL_PASSWORD = sStoreProperties.getProperty("im_mysql_password");
        IM_REDIS_HOST = sStoreProperties.getProperty("im_redis_host");
        IM_REDIS_PORT = Integer.parseInt(sStoreProperties.getProperty("im_redis_port"));
        IM_REDIS_PASSWORD = sStoreProperties.getProperty("im_redis_password");
        IM_REDIS_TIMEOUT = Integer.parseInt(sStoreProperties.getProperty("im_redis_timeout"));
        IM_REDIS_DEFAULT_DB = Integer.parseInt(sStoreProperties.getProperty("im_redis_default_db"));
    }


}
