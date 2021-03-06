package com.example.jwtserver.config;

import com.example.jwtserver.jwt.JwtAuthenticationEntryPoint;
import com.example.jwtserver.jwt.JwtAuthenticationFilter;
import com.example.jwtserver.jwt.JwtAuthorizationFilter;
import com.example.jwtserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity //스프링 시큐리티 필터(SecurityConfig)가 스프링 필터 체인에 등록됨
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserRepository userRepository;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint; //jwt 인증이 실패할 경우 처리할때 필요




    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

//        CharacterEncodingFilter filter = new CharacterEncodingFilter();
//
//        filter.setEncoding("UTF-8");
//        filter.setForceEncoding(true);
//        http.addFilterBefore(filter, CsrfFilter.class);

        // /login에서 /api/v1/login으로 변경
        JwtAuthenticationFilter authenticationFilter = new JwtAuthenticationFilter(authenticationManager());
        authenticationFilter.setFilterProcessesUrl("/api/v1/login");

        http.csrf().disable();
        http
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) //세션을 사용하지 않겠다.

                .and()
                .exceptionHandling() //예외 처리 설정 -> 동작안함
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)

                .and()
//                .addFilter(corsFilter) //crossOrigin 정책을 벗어나게(모든 요청을 허용하겠다). 인증을 하기위해 시큐리티 필터에 등록
                .formLogin().disable() //스프링 시큐리티가 제공하는 form 태그 로그인 안한다.
                .httpBasic().disable() //header의 Authorization에 id,pw를 들고 가는 방식 - disable 하고
                // Bearer방식(header의 Authorization에 토큰을 넣는 방식)을 쓸것임
                .addFilter(authenticationFilter) // /login에서 /api/v1/login으로 변경
                .addFilter(new JwtAuthenticationFilter(authenticationManager())) //AuthenticationManager 가 파라미터로 들어가야함
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), userRepository)) //AuthenticationManager 가 파라미터로 들어가야함

                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS,"/api/**").permitAll()
                .antMatchers("/api/v1/products/**").permitAll()
                .antMatchers("/api/v1/signup/**").permitAll()
                .antMatchers("/api/v1/user/**").permitAll()
                .antMatchers("/api/v1/cart/**").authenticated() //이런식으로 인증된 사용자만 접근할 수 있다.
                .antMatchers("/api/v1/user/**").access("hasRole('ROLE_USER')")
                .anyRequest().permitAll();
    }

    public CorsConfigurationSource corsConfigurationSource(){

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true); //내 서버가 응답할때 json을 자바스크립트에서 처리할 수 있게 할지를 설정하는 것
        config.addAllowedOriginPattern("*");
//        config.addAllowedOrigin("*");
        config.addAllowedHeader("*"); //모든 header에 응답을 허용하겠다.
//        config.addAllowedMethod("*"); //모든 post,get,put,delete,fetch 요청을 허용하겠다.
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("FETCH");

        //body에 유저 정보 담을때
//        config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type","userInfo"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config); // /api/** 로 들어오는 요청은 위 사항을 따르게한다.
        return source;
    }

}
