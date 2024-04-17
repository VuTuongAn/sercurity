package com.example.demo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final String[] PUBLIC_ENDPOINTS = {
            "/auth/login ",
            "/users",
            "/auth/introspect"
    };
    @Value("${jwt.signerKey}")
    private String signerKey;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request ->
                request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
                        .anyRequest().authenticated());

//        http.oauth2ResourceServer(): Cấu hình Spring Security để hoạt động như một OAuth 2.0 Resource Server. Resource Server là server chứa các tài nguyên mà client muốn truy cập, và nó sẽ xác thực request từ client bằng cách kiểm tra access token.
//                oauth2.jwt(): Cấu hình Resource Server để sử dụng JWT (JSON Web Tokens) như là access tokens. JWT là một chuẩn mở cho việc truyền thông tin an toàn giữa hai bên dưới dạng JSON object.
//                jwtConfigurer.decoder(jwtDecoder()): Đặt JwtDecoder cho Resource Server. JwtDecoder là một interface trong Spring Security dùng để giải mã JWT. Trong trường hợp này, jwtDecoder() là một method đã được định nghĩa trong cùng class, nó trả về một JwtDecoder được cấu hình để sử dụng thuật toán HS512 và secret key đã được định nghĩa.
//                Vì vậy, đoạn mã này cấu hình Spring Security để xác thực các request đến Resource Server bằng cách sử dụng JWT, và giải mã JWT bằng JwtDecoder đã được cấu hình.

        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder())));
        http.csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
//    Bean này được sử dụng để giải mã và xác thực các JWT (JSON Web Token).
    @Bean
    JwtDecoder jwtDecoder() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
        return NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }
}
