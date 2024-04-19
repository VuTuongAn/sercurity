package com.example.demo.service;

import com.example.demo.dto.request.IntrospectRequest;
import com.example.demo.dto.request.LogoutRequest;
import com.example.demo.dto.response.IntrospectResponse;
import com.example.demo.entity.InvalidatedToken;
import com.example.demo.entity.User;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.InvalidatedTokenRepository;
import com.example.demo.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.example.demo.dto.request.AuthenticationRequest;
import com.example.demo.dto.response.AuthenticationResponse;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;

    InvalidatedTokenRepository invalidatedTokenRepository;

    // @NonFinal: giúp biến này không inject vào constructor
    @NonFinal

    @Value("${jwt.signerKey}")
    protected String SIGNING_KEY;
//    @Value("${jwt.expiration}")
//    Long expiration; // Lưu vào biến môi trường


    // Xác thực đây có phải token do web server tạo ra không
    // Nếu không có chữ ký thì không thể xác thực được
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean invalid = true;
        try {
            verifyToken(token);
        } catch (AppException e) {
            invalid = false;
        }
        return IntrospectResponse.builder().valid(invalid).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated)
            throw new AppException(ErrorCode.UNTHENTICATED);

        String token = generateToken(user);
        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    // JWT sẽ tạo ra một header và một payload. Header chứa thông tin về loại token và thuật toán mã hóa. Payload chứa thông tin về người dùng.
    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        // Các data trong body đuược gọi là claim
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("vutuongan.com") // người phát hành token
                .issueTime(new Date()) // Thời gian tạo token
                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli())) // Thời gian hết hạn
//                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L)) // 1000 là đổi từ mili -> s
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
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

    // Hàm này sử dụng để nối chuỗi các scope lại với nhau bằng khoảng trắng
    private String buildScope(User user) {
        StringJoiner joiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                joiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(permission -> joiner.add(permission.getName()));
            });
        return joiner.toString();
    }

    private SignedJWT verifyToken(String token) throws ParseException, JOSEException {
        JWSVerifier verifier = new MACVerifier(SIGNING_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        // Lấy ra thời gian hết hạn của token
        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);
        // Kiểm tra xe token đã hết hạn chưa
        var verifiedToken = verified && expirationTime.after(new Date());
        // Nếu mà chữ kí và ngày hết hạn đều hợp lệ thì trả về token
        if (!verifiedToken && expirationTime.after(new Date()))
            throw new AppException(ErrorCode.UNTHENTICATED);

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNTHENTICATED);
        return signedJWT;
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        var signToken = verifyToken(request.getToken());
        // Lấy ra JWT id
        String jit = signToken.getJWTClaimsSet().getJWTID();
        // Lấy ra thời gian hết hạn của token
        Date expirationTime = signToken.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expirationTime)
                .build();
        invalidatedTokenRepository.save(invalidatedToken);

    }
}
