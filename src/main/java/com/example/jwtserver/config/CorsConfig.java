package com.example.jwtserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); //내 서버가 응답할때 json을 자바스크립트에서 처리할 수 있게 할지를 설정하는 것
        config.addAllowedOrigin("*"); // 모든 ip에 응답을 허용하겠다.
        config.addAllowedHeader("*"); //모든 header에 응답을 허용하겠다.
        config.addAllowedMethod("*"); //모든 post,get,put,delete,fetch 요청을 허용하겠다.

        source.registerCorsConfiguration("/api/**", config); // /api/** 로 들어오는 요청은 위 사항을 따르게한다.
        return new CorsFilter(source);
    }

}
