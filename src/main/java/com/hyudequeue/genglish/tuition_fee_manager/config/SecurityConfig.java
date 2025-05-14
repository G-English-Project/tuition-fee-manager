package com.hyudequeue.genglish.tuition_fee_manager.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.utility.constants.SecurityConstants;
import com.hyudequeue.genglish.tuition_fee_manager.utility.exception.CustomAccessDeniedHandler;
import com.hyudequeue.genglish.tuition_fee_manager.utility.jwt.CustomAuthenticationProvider;
import com.hyudequeue.genglish.tuition_fee_manager.utility.jwt.JwtService;
import com.hyudequeue.genglish.tuition_fee_manager.utility.jwt.JwtTokenAuthenticationFilter;
import com.hyudequeue.genglish.tuition_fee_manager.utility.jwt.JwtUsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final JwtConfig jwtConfig;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final CustomAuthenticationProvider customAuthenticationProvider;

    public SecurityConfig(JwtConfig jwtConfig, JwtService jwtService, UserRepository userRepository, CustomAuthenticationProvider customAuthenticationProvider) {
        this.jwtConfig = jwtConfig;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.customAuthenticationProvider = customAuthenticationProvider;
    }
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.authenticationProvider(customAuthenticationProvider);
        return authBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SecurityConstants.PUBLIC_URLS).permitAll()
                        .requestMatchers(SecurityConstants.TEACHER_URL_PREFIX).hasAuthority(SecurityConstants.ROLE_TEACHER)
                        .requestMatchers(SecurityConstants.STUDENT_URL_PREFIX).hasAuthority(SecurityConstants.ROLE_STUDENT)
                        .anyRequest().permitAll()
                )
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .exceptionHandling(ex -> ex
                        .accessDeniedPage(SecurityConstants.ACCESS_DENIED_PAGE)
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                )
                .logout(logout -> logout
                        .invalidateHttpSession(true)
                        .deleteCookies(SecurityConstants.SESSION_COOKIE)
                        .clearAuthentication(true)
                        .logoutRequestMatcher(new AntPathRequestMatcher(SecurityConstants.LOGOUT_URL))
                        .logoutSuccessUrl(SecurityConstants.LOGIN_SUCCESS_URL)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(
                        new JwtUsernamePasswordAuthenticationFilter(authManager, jwtConfig, jwtService, userRepository),
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilterAfter(
                        new JwtTokenAuthenticationFilter(jwtConfig, jwtService),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}

