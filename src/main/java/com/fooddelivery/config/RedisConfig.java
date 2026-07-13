package com.fooddelivery.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

    private ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL
        );
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    private GenericJackson2JsonRedisSerializer jsonSerializer() {
        return new GenericJackson2JsonRedisSerializer(redisObjectMapper());
    }

    /**
     * Primary CacheManager with per-cache TTL overrides.
     *
     * <p>TTL rationale:
     * <ul>
     *   <li><b>users</b>       – 60 min: profile data is stable; short enough to reflect deactivation.</li>
     *   <li><b>restaurants</b> – 30 min: moderate change rate; lists sorted by rating refresh after reviews.</li>
     *   <li><b>foodItems</b>   – 30 min: individual item fetches; menus evicted explicitly on writes.</li>
     *   <li><b>restaurantMenus</b> – 20 min: customer-facing menus change with toggleAvailability.</li>
     *   <li><b>orders</b>      – 15 min: order status transitions are frequent; short TTL acts as a safety net.</li>
     *   <li><b>addresses</b>   – 60 min: address book is rarely changed relative to read frequency.</li>
     *   <li><b>reviews</b>     – 30 min: review lists are heavy reads; new reviews evict explicitly.</li>
     *   <li><b>analytics</b>   – 5 min: dashboard data must be reasonably fresh without hammering the DB.</li>
     * </ul>
     */
    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer()))
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();

        cacheConfigs.put("users",
                defaultConfig.entryTtl(Duration.ofMinutes(60)));

        cacheConfigs.put("restaurants",
                defaultConfig.entryTtl(Duration.ofMinutes(30)));

        cacheConfigs.put("foodItems",
                defaultConfig.entryTtl(Duration.ofMinutes(30)));

        cacheConfigs.put("restaurantMenus",
                defaultConfig.entryTtl(Duration.ofMinutes(20)));

        cacheConfigs.put("orders",
                defaultConfig.entryTtl(Duration.ofMinutes(15)));

        cacheConfigs.put("addresses",
                defaultConfig.entryTtl(Duration.ofMinutes(60)));

        cacheConfigs.put("reviews",
                defaultConfig.entryTtl(Duration.ofMinutes(30)));

        cacheConfigs.put("analytics",
                defaultConfig.entryTtl(Duration.ofMinutes(5)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .transactionAware()
                .build();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer valueSerializer = jsonSerializer();

        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);
        template.afterPropertiesSet();

        return template;
    }
}
