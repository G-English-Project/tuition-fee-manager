package com.hyudequeue.genglish.tuition_fee_manager.config;

import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.PayOSProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    @ConfigurationProperties("payos")
    public PayOSProperties payOSProperties() {
        return new PayOSProperties();
    }
}
