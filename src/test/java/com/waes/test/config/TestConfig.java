package com.waes.test.config;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;

@TestConfiguration
@ExtendWith(MockitoExtension.class)
public class TestConfig {

    @Bean
    public CacheManager cacheManager() {
        return Mockito.mock(CacheManager.class);
    }
}
