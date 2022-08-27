package com.example.plainredis.service;

/**
 * @author aloneMan
 * @projectName plain-redis
 * @createTime 2022-08-24 22:46:14
 * @description
 */
public interface SentinelService {
    /**
     * 设置一个值
     * @param key
     * @param value
     * @return 成功返回true
     */
     Boolean setValue(String key,String value);

    /**
     * 获取一个值
     * @param key
     * @return
     */
     String getValue(String key);

    /**
     * 上锁
     */
    void block();
}
