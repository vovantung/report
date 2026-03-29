package txu.report.mainapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import txu.common.cache.RedisCacheClient;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class RedisClientConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory factory
    ) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }

    @Bean
    public Executor executor() {
        return Executors.newFixedThreadPool(2);
    }

    @Bean
    public RedisCacheClient cacheClient(RedisTemplate<String, Object> redisTemplate, Executor executor) {
        return new RedisCacheClient(redisTemplate, executor);
    }
}

