package com.example.demo.dto.request;

import com.example.demo.validator.DobConstraint;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
// Chỉnh sửa tất cả các trường thành private
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 8, max = 20, message = "USERNAME_INVALID")
    String username;
    @Size(min = 5, message = "PASSWORD_INVALID")
    String password;
    String firstName;
    String lastName;

    @DobConstraint(min = 18, message = "INVALID_DOB")
    LocalDate dateOfBirth;

}
