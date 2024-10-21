package com.santander.address.api.configurations;

import io.lettuce.core.ReadFrom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStaticMasterReplicaConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration.LettuceClientConfigurationBuilder;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfiguration {

    @Value("${aws.elasticache.redis.endpoint}")
    public String awsElastiCacheRedisEndpoint;

    @SuppressWarnings("CanBeFinal")
    @Value("${aws.elasticache.redis.with.ssl}")
    public Boolean awsElastiCacheRedisWithSsl = false;

    @Bean
    @Primary
    public CacheManager cacheManager(final RedisConnectionFactory redisConnectionFactory) {
        return RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()).build();
    }

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        RedisStaticMasterReplicaConfiguration redisStaticMasterReplicaConfiguration = new RedisStaticMasterReplicaConfiguration(awsElastiCacheRedisEndpoint);
        LettuceClientConfigurationBuilder lettuceConnectionPartialConfiguration = LettuceClientConfiguration.builder().commandTimeout(Duration.ofMinutes(1)).readFrom(ReadFrom.MASTER);

        if (awsElastiCacheRedisWithSsl) {
            return new LettuceConnectionFactory(redisStaticMasterReplicaConfiguration, lettuceConnectionPartialConfiguration.useSsl().build());
        }

        return new LettuceConnectionFactory(redisStaticMasterReplicaConfiguration, lettuceConnectionPartialConfiguration.build());
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(final RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        return redisTemplate;
    }
}
