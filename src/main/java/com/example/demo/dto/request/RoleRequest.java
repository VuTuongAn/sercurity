package com.example.demo.dto.request;

import com.example.demo.entity.Permission;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRequest {
    String name;
    String description;
    // Do chỉ cần name của permission nên không cần tạo một Set<Permission>
    Set<String> permissions;
}
