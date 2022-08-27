package com.example.plainredis.controller;

import com.example.plainredis.service.SecondKillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.plainredis.utils.CodeUtils.generateCode;

/**
 * @author aloneMan
 * @projectName plain-redis
 * @createTime 2022-08-23 20:36:00
 * @description 商品秒杀
 */

@RestController
@RequestMapping("/secKill")
@Api(tags = "商品秒杀")
public class SecondKillController {

    private final SecondKillService secondKillService;

    public SecondKillController(SecondKillService secondKillService) {
        this.secondKillService = secondKillService;
    }

    @PostMapping("setNumber")
    @ApiOperation("设置库存")
    public void setCommodityNumber(Long number) {
        secondKillService.setInStock(number);
    }

    @GetMapping("killCommodity")
    @ApiOperation("开始秒杀商品")
    public void killCommodity() {
        //生成一个USERID
        String userId = generateCode();
        String commodityKey = "commodity9806:id";
        secondKillService.killShop(userId, commodityKey);
    }


    @GetMapping("lockSecKill")
    @ApiOperation("加锁方式秒杀")
    public void lockSecKill() {
        //生成一个USERID
        String userId = generateCode();
        String commodityKey = "commodityNumber";
        secondKillService.lockKillShop(userId, commodityKey);
    }

}
