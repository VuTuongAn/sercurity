package com.example.demo.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    KHONG_XAC_DINH(9999, "Không xác định"),
    USER_EXISTED(1001, "User existed"),
    USERNAME_INVALID(1002, "Username must be between 8 and 20 characters long"),
    PASSWORD_INVALID(1002, "Password must be at least 5 characters long"),
    INVALID_KEY(1003, "Invalid key"),
    USER_NOT_EXISTED(1004, "User not existed"),
    UNTHENTICATED(1005, "Unauthenticated"),
    ;
    private int code;
    private String message;
}
