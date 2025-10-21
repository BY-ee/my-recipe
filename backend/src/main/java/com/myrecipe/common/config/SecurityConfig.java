package com.myrecipe.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    //Security 필터 체인 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // ---- REST api에서는 csrf 보호 불필요 ---- //
                .csrf(AbstractHttpConfigurer::disable)
                // ---- 세션 무효화 (stateless) ---- //
                .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // ---- cors 설정 적용 ---- //
                // .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // ---- 권한별 요청의 응답 제어 (인가) ---- //
                .authorizeHttpRequests(auth -> auth
                        // swagger 경로
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // 추가적인 경로 (로그인, 마이페이지 등)
                        // .requestMatchers("/member").authenticated()
                        // 명시된 경로 이외에는 접근 제한 필요 (임시로 제한 해제)
                        .anyRequest().permitAll()
                )
                // ---- dao, jwt 등 로그인 필터 추가 필요 ---- //
                .build();
    }

    // cors 설정
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8081")); // url 허용
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH")); // http 메서드 허용
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept")); // 요청 헤더 허용
        configuration.setExposedHeaders(List.of("Authorization")); // JWT 헤더 허용
        configuration.setAllowCredentials(true); // 인증 정보 포함 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
