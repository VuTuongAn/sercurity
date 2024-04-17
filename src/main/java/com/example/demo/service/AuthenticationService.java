package com.example.demo.service;

import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import dto.request.AuthenticationRequest;
import dto.response.AuthenticationResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
     UserRepository userRepository;

     // @NonFinal: giúp biến này không inject vào constructor
     @NonFinal
     protected static final String SIGNING_KEY = "Nt2tXsDSMZlT6jhT5tl69Gb6A10mNeKeRKe/0KgLqZjuYPftws+UKObMU3ReZbH3";
//    @Value("${jwt.expiration}")
//    Long expiration; // Lưu vào biến môi trường

//    @Value("${jwt.secretKey}")
//    String secretKey;
    public AuthenticationResponse authenticate(AuthenticationRequest request){
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated)
            throw new AppException(ErrorCode.UNTHENTICATED);
        String token = generateToken(request.getUsername());
        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }
    // JWT sẽ tạo ra một header và một payload. Header chứa thông tin về loại token và thuật toán mã hóa. Payload chứa thông tin về người dùng.
    private String generateToken(String username){
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        // Các data trong body đuược gọi là claim
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .issuer("vutuongan.com") // người phát hành token
                .issueTime(new Date()) // Thời gian tạo token
                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli())) // Thời gian hết hạn
//                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L)) // 1000 là đổi từ mili -> s
                .claim("customClaim", "custom")
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(SIGNING_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
