package com.example.plainredis.service.impl;

import com.example.plainredis.service.PhoneVerifyService;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.example.plainredis.utils.CodeUtils.generateCode;

/**
 * @author aloneMan
 * @projectName plain-redis
 * @createTime 2022-08-23 11:47:10
 * @description
 */
@Service
@Transactional
public class PhoneVerifyServiceImpl implements PhoneVerifyService {
    private Random random = new Random();
    private ValueOperations<String, Object> vo;

    private RedisTemplate redisTemplate;

    public PhoneVerifyServiceImpl(RedisTemplate redisTemplate) {
        vo = redisTemplate.opsForValue();
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getCode(String number) {
        String codeKey = new StringBuffer().append("Verify").append(number).append(":code").toString();
        String timeLimitKey = new StringBuffer().append("Verify").append(number).append(":expired").toString();
        String code = (String) vo.get(codeKey);
        //如果不存在验证码，生成后保存到redis
        if (code == null) {
            //记录十分钟内获生成验证码次数
            Integer timeLimitValue = (Integer) vo.get(timeLimitKey);
            if (timeLimitValue == null) {
                vo.set(timeLimitKey, 1, 60 * 10, TimeUnit.SECONDS);
            } else if (timeLimitValue > 3) {
                System.out.println("十分钟内获取次数超过三次，请稍后再试.");
                return null;
            } else {
                vo.increment(timeLimitKey);
            }
            code = generateCode();
            //设置验证码，有效时间长2 minute
            vo.set(codeKey, code, 60, TimeUnit.SECONDS);

            return code;
        }
        return code;
    }



    @Override
    public Boolean verify(String phoneNumber, String code) {
        String codeKey = new StringBuffer().append("Verify").append(phoneNumber).append(":code").toString();
        String codeValue = (String) vo.get(codeKey);
        if (codeValue == null) {
            System.out.println("请先获取验证码!");
            return false;
        }
        if (code.equals(codeValue)) {
            vo.getAndDelete(codeKey);
            return true;
        }
        return false;
    }



}
