package com.example.demo.controller;

import com.example.demo.dto.request.PermissionRequest;
import com.example.demo.dto.response.ApiResponse;
import com.example.demo.dto.response.PermissionResponse;
import com.example.demo.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionController {
        PermissionService permissionService;

        @PostMapping
        ApiResponse<PermissionResponse> createPermission(@RequestBody PermissionRequest request){
                return ApiResponse.<PermissionResponse>builder().result(permissionService.createPermission(request)).build();
        }

        @GetMapping
        ApiResponse<List<PermissionResponse>> getAllPermissions(){
                return ApiResponse.<List<PermissionResponse>>builder().result(permissionService.getAllPermissions()).build();
        }

        @DeleteMapping("/{permissionId}")
        ApiResponse<Void> deletePermission(@PathVariable("permissionId") String permissionId){
                permissionService.deletePermission(permissionId);
                return ApiResponse.<Void>builder().build();
        }


}
