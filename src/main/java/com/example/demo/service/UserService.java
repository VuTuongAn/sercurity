package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repository.UserRepository;
import dto.request.UserCreationRequest;
import dto.request.UserUpdateRequest;
import dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
// Các biến mà không khai báo final thì nó sẽ tự động khởi tạo giá trị
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
     UserRepository userRepository;

     UserMapper userMapper;
    public User createUser(UserCreationRequest request){
        if (userRepository.existsByUsername(request.getUsername())){
            throw new AppException(ErrorCode.USER_EXISTED);
            // Nếu mà trả về 9999 là lỗi mình chưa bắt
//            throw new RuntimeException("Không bắt được lỗi");
        }
        User user = userMapper.toEntity(request);
        // Strength: 10 càng lớn thì càng an toàn nhưng càng chậm
        PasswordEncoder encoder = new BCryptPasswordEncoder(10);
        user.setPassword(encoder.encode(request.getPassword()));
        return userRepository.save(user);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public void deleteUser(String userId){
        userRepository.deleteById(userId);
    }
    public UserResponse updateUser(String userId, UserUpdateRequest request){
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        userMapper.updateUser(user, request);
        return userMapper.toResponse(userRepository.save(user));
    }

    public UserResponse getUser(String userId){
       return userMapper.toResponse( userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")));
    }

}
