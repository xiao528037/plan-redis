package com.example.plainredis.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.redis.core.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author aloneMan
 * @projectName plain-redis
 * @createTime 2022-08-22 20:37:10
 * @description
 */

@RestController
@RequestMapping("/redis")
@Api(tags = "redis")
public class RedisController {

    private RedisTemplate redisTemplate;

    private StringRedisTemplate stringRedisTemplate;

    private ValueOperations<String, String> vo;
    private SetOperations<String, String> so;
    private ListOperations<String, String> lo;
    private HashOperations<String, String, String> ho;
    private ZSetOperations<String, String> zo;

    public RedisController(RedisTemplate redisTemplate, StringRedisTemplate stringRedisTemplate) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
        vo = stringRedisTemplate.opsForValue();
        so = redisTemplate.opsForSet();
        lo = redisTemplate.opsForList();
        ho = redisTemplate.opsForHash();
        zo = redisTemplate.opsForZSet();
    }


    @GetMapping("getByKey")
    @ApiOperation("通过key获取")
    public String getByKey(String key) {
        String result = vo.get(key);
        return result;
    }

    @DeleteMapping("deleteByKey")
    @ApiOperation("删除一个值")
    public String deleteByKey(String key) {
        return vo.getAndDelete(key);
    }

    @PostMapping("addValue")
    @ApiOperation("添加一个值")
    public void addValue(String key, String value) {
        vo.set(key, value);
    }

    @PostMapping("addValues")
    @ApiOperation("添加一个数组")
    public void addValue(String key, String... values) {
        lo.leftPushAll(key, values);
    }

    @GetMapping
    @ApiOperation("获取一个数组")
    public List<String> getKeyForList(String key) {
        List<String> range = lo.range(key, 0, -1);
        return range;
    }

    @PostMapping("addValueExpire")
    public void addValueExpire(String key, String value, Long time) {
        vo.set(key, value, time, TimeUnit.SECONDS);
    }


}
