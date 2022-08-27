package com.example.plainredis.utils;

import java.util.Random;

/**
 * @author aloneMan
 * @projectName plain-redis
 * @createTime 2022-08-23 20:44:00
 * @description
 */
public class CodeUtils {
    private static Random random = new Random();

    /**
     * 生成验证码
     *
     * @return 返回验证码
     */
    public static String generateCode() {
        return Integer.toString(random.nextInt(1000000));
    }
}
