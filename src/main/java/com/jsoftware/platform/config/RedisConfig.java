package com.jsoftware.platform.config;

//import org.springframework.data.redis.connection.RedisClusterConfiguration;
//import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

import java.time.Duration;

@Configuration
@EnableRedisHttpSession
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private String redisPort;

    @Value("${spring.redis.password}")
    private String redisPassword;

    @Bean(name = "redisProperties")
    @Primary
    @ConfigurationProperties(prefix = "spring.redis")
    public RedisConnectionFactory redisConnectionFactory() {
//        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisHost);
        redisStandaloneConfiguration.setPort(Integer.parseInt(redisPort));
        redisStandaloneConfiguration.setPassword(redisPassword);

        return new LettuceConnectionFactory(redisStandaloneConfiguration, getLettuceClientConfiguration());
    }

    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setDefaultSerializer(new JdkSerializationRedisSerializer());

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public static ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }

    @Value("${spring.redis.cache.host}")
    private String redisCacheHost;

    @Value("${spring.redis.cache.port}")
    private String redisCachePort;

    @Value("${spring.redis.cache.password}")
    private String redisCachePassword;

    @Bean(name = "redisCacheProperties")
    @ConfigurationProperties(prefix = "spring.redis.cache")
    public RedisConnectionFactory redisCacheConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisCacheHost);
        redisStandaloneConfiguration.setPort(Integer.parseInt(redisCachePort));
        redisStandaloneConfiguration.setPassword(redisCachePassword);

        return new LettuceConnectionFactory(redisStandaloneConfiguration, getLettuceClientConfiguration());
    }

    @Bean
    public RedisTemplate<String, Object> redisCacheTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisCacheConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer(Object.class)); // https://mongsil-jeong.tistory.com/25
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setDefaultSerializer(new JdkSerializationRedisSerializer());

        return redisTemplate;
    }

    @Bean
    public static ConfigureRedisAction configureCacheRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("JSESSIONID");
        serializer.setCookiePath("/");
        serializer.setUseBase64Encoding(false);
        serializer.setDomainNamePattern("^.+?\\.(\\w+\\.[a-z]+)$");
        return serializer;
    }

    public LettuceClientConfiguration getLettuceClientConfiguration() {
        return LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofMinutes(1))
                .shutdownTimeout(Duration.ZERO)
                .build();
    }

}