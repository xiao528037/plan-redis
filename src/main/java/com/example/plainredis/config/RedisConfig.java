package com.example.plainredis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author aloneMan
 * @projectName plain-redis
 * @createTime 2022-08-22 20:58:51
 * @description
 */

@Configuration
public class RedisConfig {

/*    @Bean(name = "listenerContainer")
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(connectionFactory);
        return redisMessageListenerContainer;
    }*/

    /**
     * 防止存入Redis中出现乱码
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public RedisTemplate redisTemplate(RedisTemplate redisTemplate) {
        // json 序列化配置
        Jackson2JsonRedisSerializer jsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 识别所有字段 PropertyAccessor 识别任何字段 JsonAutoDetect
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        jsonRedisSerializer.setObjectMapper(objectMapper);

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key 采用 String的序列化
        redisTemplate.setKeySerializer(stringRedisSerializer);
        // hash 的key采用String的序列化
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        // hash 的 value 采用 String 的序列化
        redisTemplate.setHashValueSerializer(jsonRedisSerializer);
        // value 序列化方式采用 Jackson
        redisTemplate.setValueSerializer(stringRedisSerializer);
        //开启事物
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();

        ClusterServersConfig clusterServersConfig = config.useClusterServers().addNodeAddress("redis://127.0.0.1:6379", "redis://127.0.0.1:6380",
                "redis://127.0.0.1:6381", "redis://127.0.0.1:6389", "redis://127.0.0.1:6390", "redis://127.0.0.1:6391");
        clusterServersConfig.setPassword("xiaojiebin");
        //修改watch dog的时间
        config.setLockWatchdogTimeout(4000 * 10);
        RedissonClient redissonClient = Redisson.create(config);

        return redissonClient;
    }
}
