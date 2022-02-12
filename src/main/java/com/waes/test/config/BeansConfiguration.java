package com.waes.test.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * General {@link Bean} configuration class.
 *
 * @author jonathanadepaula
 */
@Configuration
public class BeansConfiguration {

    /**
     * Get an {@link ObjectMapper} bean.
     *
     * @return {@link ObjectMapper}
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    /**
     * Get an {@link OkHttpClient} bean.
     *
     * @return {@link OkHttpClient}
     */
    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient();
    }
}
