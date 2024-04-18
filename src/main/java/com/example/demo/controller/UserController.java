package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.request.UserCreationRequest;
import com.example.demo.dto.request.UserUpdateRequest;
import com.example.demo.dto.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService;

    @PostMapping
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request){
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.createUser(request));
        return apiResponse;
    }
    // hasRole mặc định spring sẽ tìm những prefix là ROLE_ để phân quyền
//    @PreAuthorize("hasRole('ADMIN')")
    // Có thể sử dụng role CREATE_DATA hoặc ADMIN vì nó đang được lưu trong token tại claim scope
    @PreAuthorize("hasAuthority('CREATE_DATA')")
    @GetMapping
    ApiResponse<List<UserResponse>> getAllUsers(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("Username: {}", authentication.getName());
        authentication.getAuthorities().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));
        return ApiResponse.<List<UserResponse>>builder().result(userService.getAllUsers()).build();
    }
    // Kiểm tra nếu đúng là user đăng nhập thì mới truy cập được id của user đó
    @PostAuthorize("returnObject.id == authentication.name")
    @GetMapping("/{userId}")
    UserResponse getUser(@PathVariable("userId") String userId){
        return userService.getUser(userId);
    }

    @PutMapping("/{userId}")
    UserResponse updateUser(@PathVariable("userId") String userId, @RequestBody UserUpdateRequest request){
        return userService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    String deleteUser(@PathVariable("userId") String userId){
        userService.deleteUser(userId);
        return "User deleted successfully";
    }

    @GetMapping("/my-info")
    ApiResponse<UserResponse> getMyInfo(){
        return ApiResponse.<UserResponse>builder().result(userService.getMyInfo()).build();
    }

}
