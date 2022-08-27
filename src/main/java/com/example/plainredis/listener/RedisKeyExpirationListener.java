package com.example.plainredis.listener;


import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

/**
 * @author aloneMan
 * @projectName plain-redis
 * @createTime 2022-08-22 20:50:22
 * @description
 */
//@Component
public class
RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String s = message.toString();
        System.out.println("过期的Key" + s);
    }
}
