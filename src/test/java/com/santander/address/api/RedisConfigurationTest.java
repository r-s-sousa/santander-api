package com.santander.address.api;

import com.santander.address.api.configurations.RedisConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class RedisConfigurationTest {

   @SuppressWarnings("CanBeFinal")
   @Value("${aws.elasticache.redis.endpoint}")
   private String awsElastiCacheRedisEndpoint = "localhost";

   @SuppressWarnings("CanBeFinal")
   @Value("${aws.elasticache.redis.with.ssl}")
   private Boolean awsElastiCacheRedisWithSsl = false;

   private RedisConfiguration redisConfiguration;

   @Mock
   private RedisConnectionFactory redisConnectionFactory;

   @BeforeEach
   void setUp() {
       MockitoAnnotations.openMocks(this);
       redisConfiguration = new RedisConfiguration();
       redisConfiguration.awsElastiCacheRedisEndpoint = awsElastiCacheRedisEndpoint;
   }

   @Test
   void testCacheManager() {
       CacheManager cacheManager = redisConfiguration.cacheManager(redisConnectionFactory);
       assertNotNull(cacheManager);
       assert(cacheManager instanceof RedisCacheManager);
   }

    @Test
    void testLettuceConnectionFactory() {
        LettuceConnectionFactory lettuceConnectionFactory = redisConfiguration.lettuceConnectionFactory();
        assertNotNull(lettuceConnectionFactory);

        RedisStandaloneConfiguration redisStaticMasterReplicaConfiguration = lettuceConnectionFactory.getStandaloneConfiguration();
        assertNotNull(redisStaticMasterReplicaConfiguration);
        assertEquals(awsElastiCacheRedisEndpoint, redisStaticMasterReplicaConfiguration.getHostName());

        LettuceClientConfiguration lettuceClientConfiguration = lettuceConnectionFactory.getClientConfiguration();
        assertNotNull(lettuceClientConfiguration);
        assertEquals(Duration.ofMinutes(1), lettuceClientConfiguration.getCommandTimeout());

        // Check if SSL is configured as expected
        if (awsElastiCacheRedisWithSsl) {
            assertTrue(lettuceClientConfiguration.isUseSsl());
        } else {
            assertFalse(lettuceClientConfiguration.isUseSsl());
        }
    }

   @Test
   void testRedisTemplate() {
       RedisTemplate<String, Object> redisTemplate = redisConfiguration.redisTemplate(redisConnectionFactory);
       assertNotNull(redisTemplate);
       assert(Objects.equals(redisTemplate.getConnectionFactory(), redisConnectionFactory));
   }

}
