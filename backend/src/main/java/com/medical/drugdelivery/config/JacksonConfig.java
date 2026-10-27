package com.medical.drugdelivery.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson 全局日期格式配置
 * application.yml 里的 spring.jackson.date-format 只对 java.util.Date 生效，
 * 实体类里用的 LocalDateTime 默认会序列化成 "2026-10-20T10:15:00" 这种带T的ISO格式，
 * 前端表格直接展示会很丑，所以在这里统一改成 "yyyy-MM-dd HH:mm:ss"
 */
@Configuration
public class JacksonConfig {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return builder -> builder
                .serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(FORMATTER))
                .deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(FORMATTER));
    }
}
