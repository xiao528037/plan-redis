package com.example.plainredis.service;

/**
 * @author aloneMan
 * @projectName plain-redis
 * @createTime 2022-08-23 20:37:18
 * @description
 */
public interface SecondKillService {
    /**
     * 设置库存
     *
     * @param number
     *         库存数量
     */
    void setInStock(Long number);

    /**
     * 秒杀商品
     * @param userId 用户ID
     * @param shopKey 商品Key
     */
    void killShop(String userId, String shopKey);

    String lockKillShop(String userId, String commodityKey);
}
