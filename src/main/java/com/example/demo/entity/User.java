package com.example.demo.entity;

import com.example.demo.validator.DobConstraint;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String username;
    String password;
    String firstName;
    String lastName;
    LocalDate dateOfBirth;
    // Đảm bảo các role không trùng lặp chỉ có 1 unique role
    @ManyToMany
    Set<Role> roles;
}
