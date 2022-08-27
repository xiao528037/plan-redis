package com.example.plainredis;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class PlainRedisApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    @Resource
    private RedissonClient redissonClient;

    @Test
    void contextLoads() {
        RedisConnection connection = redisConnectionFactory.getConnection();
        HyperLogLogOperations<String, String> s = stringRedisTemplate.opsForHyperLogLog();
        s.add("g_k1", "nihao", "who is dog", "hello world");
    }

    @Test
    public void testBloom() {
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        RLock lock = redissonClient.getLock("k1");
        try {
            boolean resultLock = lock.tryLock(30, 10, TimeUnit.SECONDS);
            if (resultLock) {
                String commodityNumber = stringStringValueOperations.get("commodityNumber");
                //库存数量为0
                if (Integer.parseInt(commodityNumber) == 0) {
                    System.out.println("没有商品库存");
                } else {
                    stringStringValueOperations.decrement("commodityNumber");
                    System.out.println("购买成功");
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            //释放锁
            lock.unlock();
        }

    }


    @Test
    public void testTransactional() {

        //进行事物操作
        List result = (List) redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //开启事物
                operations.multi();
                try {
                    operations.opsForSet().add("key1", "hahahaah");
                    operations.opsForValue().set(null, "");
                } catch (Exception e) {
                    //回滚事物
                    operations.discard();
                }
                //执行事物
                return operations.exec();
            }
        });
        if (result != null) {
            System.out.println("添加成功");
        }
    }


    @Test
    public void bloomFilter() {
        RBloomFilter<Object> keysFilter = redissonClient.getBloomFilter("keysFilter");
        keysFilter.tryInit(5000000, 0.01);
        boolean key1 = keysFilter.add("key1");
        boolean key12 = keysFilter.add("key1");
        System.out.println(key1 + " " + key12);
        System.out.println(keysFilter.count());
    }

    public static void main(String[] args) {

      /*  BloomFilter<Integer> integerBloomFilter = BloomFilter.create(Funnels.integerFunnel(), 1000000, 0.003);
        for (int i = 0; i < 1000000; i++) {
            boolean put = integerBloomFilter.put(i);
        }
        int count = 0;
        for (int i = 1000000; i < 1100000; i++) {
            if (integerBloomFilter.mightContain(i)) {
                count++;
                System.out.println(i + "误判了");
            }
        }
        System.out.println(count);*/
    }

}
