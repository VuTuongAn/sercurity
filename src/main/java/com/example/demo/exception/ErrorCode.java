package com.example.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    KHONG_XAC_DINH(9999, "Không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTED(1001, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1002, "Username must be between 8 and 20 characters long", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1002, "Password must be at least 5 characters long", HttpStatus.BAD_REQUEST),
    INVALID_KEY(1003, "Invalid key", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1004, "User not existed", HttpStatus.NOT_FOUND),
    UNTHENTICATED(1005, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1006, "Unauthorized", HttpStatus.FORBIDDEN),
    INVALID_DOB(1007, "Invalid date of birth", HttpStatus.BAD_REQUEST),
    ;
    private int code;
    private String message;
    private HttpStatusCode httpStatusCode;
}
