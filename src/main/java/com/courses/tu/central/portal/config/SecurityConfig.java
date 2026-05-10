/* package com.courses.tu.central.portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/images/**", "/js/**").permitAll() 
                .anyRequest().permitAll()
            )
            .formLogin(form -> form.disable()) // ตัวสั่งปิดหน้าขาวๆ อยู่ตรงนี้ครับ!
            .httpBasic(basic -> basic.disable());
        return http.build();
    }
}
*/