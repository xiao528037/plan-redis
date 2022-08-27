package com.example.plainredis.controller;

import com.example.plainredis.service.SentinelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.example.plainredis.utils.CodeUtils.generateCode;

/**
 * @author aloneMan
 * @projectName plain-redis
 * @createTime 2022-08-24 22:44:06
 * @description
 */

@Api("sentinel测试")
@RestController
@RequestMapping("/sentinel")
public class SentinelController {
    private final SentinelService sentinelService;

    public SentinelController(SentinelService sentinelService) {
        this.sentinelService = sentinelService;
    }

    @PostMapping("setValue")
    @ApiOperation(value = "设置一个值")
    public Boolean setValue(String key, String value) {
//        key = generateCode();
//        value = generateCode();
        if (key == null) {
            return false;
        }
        return sentinelService.setValue(key, value);
    }

    @PostMapping("getValue")
    @ApiOperation(value = "获取一个值")
    public String getValue(String key) {
        return sentinelService.getValue(key);
    }
}
