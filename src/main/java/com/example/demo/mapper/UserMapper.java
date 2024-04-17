package com.example.demo.mapper;

import com.example.demo.entity.User;
import dto.request.UserCreationRequest;
import dto.request.UserUpdateRequest;
import dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
// Báo cho mapper biết rằng đây là một DI
public interface UserMapper {
    User toEntity(UserCreationRequest request);
    // Trong trường hợp mà hai cái này không giống nhau thì ta sẽ phải sử dụng @Mapping
    // @Mapping(source = "firstName", target = "lastName")
    // Trong trường hợp mà bỏ qua không map thì ta sẽ sử dụng ignore = true
    // @Mapping(target = "lastName", ignore = true)
    UserResponse toResponse(User user);
    void updateUser(@MappingTarget User user, UserUpdateRequest request);

}
