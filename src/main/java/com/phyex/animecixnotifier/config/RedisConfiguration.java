package com.phyex.animecixnotifier.config;

import com.phyex.animecixnotifier.dto.SessionInfo;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class RedisConfiguration {

    @Bean
    public RedisTemplate<String, SessionInfo> redisTemplate(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper, InjectionPoint injectionPoint) {

        RedisTemplate<String, SessionInfo> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // String serializer for keys (standard & hash)
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);


        JacksonJsonRedisSerializer<SessionInfo> jsonSerializer =
                new JacksonJsonRedisSerializer<SessionInfo>(objectMapper, SessionInfo.class);

        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }
}
