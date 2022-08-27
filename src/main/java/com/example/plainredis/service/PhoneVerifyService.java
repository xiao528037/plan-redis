package com.example.plainredis.service;

/**
 * @author aloneMan
 * @projectName plain-redis
 * @createTime 2022-08-23 11:46:49
 * @description
 */
public interface PhoneVerifyService {
    public String getCode(String number);

    public Boolean verify(String phoneNumber, String code);
}
