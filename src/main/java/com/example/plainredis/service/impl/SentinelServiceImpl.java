package com.example.plainredis.service.impl;

import com.example.plainredis.service.SentinelService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

/**
 * @author aloneMan
 * @projectName plain-redis
 * @createTime 2022-08-24 22:47:34
 * @description
 */

@Service
public class SentinelServiceImpl implements SentinelService {

    private final RedisTemplate redisTemplate;
    private final ValueOperations vo;

    private ThreadPoolExecutor threadPoolExecutor;

    public SentinelServiceImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.vo = redisTemplate.opsForValue();
        threadPoolExecutor = new ThreadPoolExecutor(10, 20, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
    }


    @Override
    public Boolean setValue(String key, String value) {
        Future<Boolean> result = threadPoolExecutor.submit(() -> {
            if (vo.get(key) != null) {
                System.out.println("值已经存在");
                return false;
            }
            vo.set(key, value);
            return true;
        });
        try {
            return result.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getValue(String key) {
        String value = (String) vo.get(key);
        return value;
    }

    @Override
    public void block() {
       vo.setIfAbsent("k1","v2",1,TimeUnit.SECONDS);
    }
}
