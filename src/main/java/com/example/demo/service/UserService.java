package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.enums.Role;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.dto.request.UserCreationRequest;
import com.example.demo.dto.request.UserUpdateRequest;
import com.example.demo.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
// Các biến mà không khai báo final thì nó sẽ tự động khởi tạo giá trị
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
     UserRepository userRepository;
     UserMapper userMapper;
     PasswordEncoder passwordEncoder;
     RoleRepository roleRepository;
    public UserResponse createUser(UserCreationRequest request){
        if (userRepository.existsByUsername(request.getUsername())){
            throw new AppException(ErrorCode.USER_EXISTED);
            // Nếu mà trả về 9999 là lỗi mình chưa bắt
//             throw new RuntimeException("Không bắt được lỗi");
        }
        User user = userMapper.toEntity(request);
        // Strength: 10 càng lớn thì càng an toàn nhưng càng chậm
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
//        user.setRoles(roles);
        return userMapper.toResponse(userRepository.save(user));
    }

    public List<UserResponse> getAllUsers(){
        return userRepository.findAll().stream()
                .map(userMapper::toResponse).toList();
    }

    public UserResponse getMyInfo() {
        // Sau khi đăng nhập xác nhận thành công thì sẽ lưu vào contexthoder và lấy ra qua getContext
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toResponse(user);
    }

    public void deleteUser(String userId){
        userRepository.deleteById(userId);
    }
    public UserResponse updateUser(String userId, UserUpdateRequest request){
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        var role = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(role));
        userMapper.updateUser(user, request);
        return userMapper.toResponse(userRepository.save(user));
    }

    public UserResponse getUser(String userId){
       return userMapper.toResponse( userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")));
    }



}
