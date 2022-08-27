package com.example.plainredis.service.impl;

import com.example.plainredis.service.SecondKillService;
import org.redisson.RedissonLock;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.plainredis.utils.CodeUtils.generateCode;

/**
 * @author aloneMan
 * @projectName plain-redis
 * @createTime 2022-08-23 20:40:04
 * @description
 */

@Service
public class SecondKillServiceImpl implements SecondKillService {


    private AtomicInteger atomicInteger = new AtomicInteger(0);
    private RedisTemplate redisTemplate;

    private ValueOperations valueOperations;

    private SetOperations setOperations;

    private RedissonClient redissonClient;

    public SecondKillServiceImpl(RedisTemplate redisTemplate, RedissonClient redissonClient) {
        this.valueOperations = redisTemplate.opsForValue();
        this.setOperations = redisTemplate.opsForSet();
        this.redisTemplate = redisTemplate;
        this.redissonClient = redissonClient;
    }

    @Override
    public void setInStock(Long number) {
        String s = generateCode();
        String shopNumberKey = new StringBuffer().append("commodity").append("9806").append(":id").toString();
        valueOperations.set(shopNumberKey, Long.toString(number));
    }

    @SuppressWarnings("AlibabaTransactionMustHaveRollback")
    @Override
//    @Transactional
    public void killShop(String userId, String shopKey) {
        List result = (List) redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.watch(shopKey);
                String commodityNum = (String) operations.opsForValue().get(shopKey);
                //秒杀是否开始
                if (commodityNum == null) {
                    System.out.println("秒杀没有开始");
                } else if (Long.parseLong(commodityNum) <= 0) {
                    System.out.println("库存不足");
                } else if (operations.opsForSet().isMember("userId", userId)) {
                    System.out.println("不能重复抢购商品");
                } else {
                    try {
                        operations.multi();
                        operations.opsForSet().add("userId", userId);
                        operations.opsForValue().decrement(shopKey);
                    } catch (Exception e) {
                        System.out.println("执行错误");
                    } finally {
                        return operations.exec();
                    }
                }
                return null;
            }
        });
        if (result != null) {
            System.out.println("抢购成功");
        }
    }

    @Override
    public String lockKillShop(String userId, String commodityKey) {

        Long offset = Long.parseLong(userId);
        if (valueOperations.get("inStock") == null) {
            System.out.println("秒杀还没有开始");
            return "秒杀还没有开始";
        }
        Boolean toByUser = valueOperations.getBit("ToByUser", offset);
        if (toByUser) {
            System.out.println(atomicInteger.incrementAndGet());
            return "不能重复购买";
        }
        //获取锁
        RLock commodityLock = redissonClient.getLock("commodityLock");
        try {
            boolean b = commodityLock.tryLock(10000, TimeUnit.MILLISECONDS);
            //获取锁成功
            if (b) {
                //获取商品的库存
                String inStock = (String) valueOperations.get("inStock");
                if (Integer.parseInt(inStock) == 0) {
                    //证明商品秒杀完毕,设置十分钟后清理掉数据
//                    valueOperations.getAndExpire("inStock", 10, TimeUnit.MINUTES);
//                    valueOperations.getAndExpire("ToByUser", 10, TimeUnit.MINUTES);
                    System.out.println("库存不足");
                    return "库存不足,购买失败";
                }
                valueOperations.decrement("inStock");
                //记录已经购买
                valueOperations.setBit("ToByUser", offset, true);
                return "购买成功";
            } else {
                System.out.println(atomicInteger.incrementAndGet());
                return "抢购失败,继续抢购";
            }
        }/* catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/ catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (commodityLock.isLocked() && commodityLock.isHeldByCurrentThread()) {
                //释放锁
//            System.out.println("释放锁");
                commodityLock.unlock();
            }
        }
    }

}
