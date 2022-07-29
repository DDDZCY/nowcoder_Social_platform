package com.nowcode.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String ,Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String ,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        //配置从java数据转化到redis数据的转化方式

        //设置key的转化方式
        template.setKeySerializer(RedisSerializer.string());
        //设置value的转化方式
        template.setValueSerializer(RedisSerializer.json());
        //设置hash的key转化方式
        template.setHashKeySerializer(RedisSerializer.string());
        //设置hash的value转化方式
        template.setHashValueSerializer(RedisSerializer.json());
        template.afterPropertiesSet();
        return template;
    }
}
