package com.walker.learning.store.core;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * RedisConnInstance
 *
 * @author walker lee
 * @date 2019/4/8
 */
public class RedisConnInstance {

    private static RedisConnInstance sRedisConnInstance;

    private static JedisPool sJedisPool;

    private RedisConnInstance() {
        sJedisPool = new JedisPool(new JedisPoolConfig(), StoreConfig.IM_REDIS_HOST, StoreConfig.IM_REDIS_PORT);
    }


    public static RedisConnInstance getInstance() {
        if (null == sRedisConnInstance) {
            synchronized (SQLConnInstance.class) {
                if (null == sRedisConnInstance) {
                    sRedisConnInstance = new RedisConnInstance();
                }
            }
        }
        return sRedisConnInstance;
    }


    public void set(String key, String value) {
        try (Jedis jedis = sJedisPool.getResource()) {
            jedis.select(StoreConfig.IM_REDIS_DEFAULT_DB);
            jedis.set(key, value);
        }
    }

    public String get(String key) {
        try (Jedis jedis = sJedisPool.getResource()) {
            jedis.select(StoreConfig.IM_REDIS_DEFAULT_DB);
            return jedis.get(key);
        }
    }
}
