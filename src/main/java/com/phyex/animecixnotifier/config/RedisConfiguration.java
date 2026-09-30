package com.phyex.animecixnotifier.config;

import org.springframework.beans.factory.InjectionPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ResolvableType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.ObjectMapper;

import java.util.Objects;

@Configuration
public class RedisConfiguration {

    @Bean
    @Scope("prototype")
    public <K, V> RedisTemplate<K, V> genericRedisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper, InjectionPoint injectionPoint) {

        RedisTemplate<K, V> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // String serializer for keys (standard & hash)
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        template.setHashValueSerializer(new JacksonJsonRedisSerializer<>(objectMapper, getClazz(injectionPoint)));

        template.afterPropertiesSet();
        return template;
    }

    private <V> Class<V> getClazz(InjectionPoint injectionPoint) {
        ResolvableType resolvableType = null;
        if (Objects.nonNull(injectionPoint.getMethodParameter())) {
            resolvableType = ResolvableType.forMethodParameter(injectionPoint.getMethodParameter());
        } else if (Objects.nonNull(injectionPoint.getField())) {
            resolvableType = ResolvableType.forField(injectionPoint.getField());
        }

        if (Objects.isNull(resolvableType))
            throw new RuntimeException();
        return (Class<V>) resolvableType.getGeneric(1).resolve();
    }

    @Bean
    public RedisTemplate<String, String> stringRedisTemplate(RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer serializer = new StringRedisSerializer();

        template.setKeySerializer(serializer);
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();

        return template;
    }
}
