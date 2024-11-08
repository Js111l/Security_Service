package com.ecom.security_service.config;

import com.ecom.security_service.model.UserSessionModel;
import com.ecom.security_service.service.JedisConfig;
import com.esotericsoftware.kryo.Kryo;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.codec.KryoCodec;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
public class RedisClientConfig {

    //SETUP JEDIS CONFIG
    @Bean
    public JedisConfig getClient() {
        return new JedisConfig("localhost", 6379);
    }
}
