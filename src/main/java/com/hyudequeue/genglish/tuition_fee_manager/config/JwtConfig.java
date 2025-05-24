package com.hyudequeue.genglish.tuition_fee_manager.config;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtConfig {
    @Value("${jwt.url}")
    private String url;

    @Value("${jwt.header}")
    private String header;

    @Value("${jwt.prefix}")
    private String prefix;

    @Value("${jwt.expiration}")
    private int expiration;

    @Value("${jwt.secret}")
    private String secret;

}
