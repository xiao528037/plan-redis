package com.example.plainredis.controller;

import com.example.plainredis.service.PhoneVerifyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author aloneMan
 * @projectName plain-redis
 * @createTime 2022-08-23 11:41:47
 * @description
 */
@RestController
@RequestMapping("/phone")
@Api(tags = "手机验证")
public class PhoneVerifyController {

    @Resource
    private PhoneVerifyService phoneVerifyService;

    @GetMapping("getCode")
    @ApiOperation(value = "获取验证码")
    public String getCode(String phoneNumber) {
        //获取验证码
        String code = phoneVerifyService.getCode(phoneNumber);
        return code;
    }

    @GetMapping("verifyCode")
    @ApiOperation(value = "校验验证码")
    public Boolean verifyCode(String phoneNumber, String code) {

        return phoneVerifyService.verify(phoneNumber, code);
    }
}
