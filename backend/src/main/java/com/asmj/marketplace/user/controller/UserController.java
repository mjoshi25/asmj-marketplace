package com.asmj.marketplace.user.controller;

import com.asmj.marketplace.common.response.ApiResponse;
import com.asmj.marketplace.user.dto.UserDtos;
import com.asmj.marketplace.user.model.User;
import com.asmj.marketplace.user.service.UserService;
import com.asmj.marketplace.upload.service.CloudinaryUploadService;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService users;
    private final CloudinaryUploadService uploads;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<User> me(Authentication authentication) {
        return ApiResponse.ok("User", safe(users.me(authentication.getName())));
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<User> update(Authentication authentication, @Valid @RequestBody UserDtos.UpdateProfileRequest request) {
        return ApiResponse.ok("Profile updated", safe(users.updateProfile(authentication.getName(), request)));
    }

    @PostMapping("/me/photo")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<User> photo(Authentication authentication, @RequestParam("file") MultipartFile file) {
        var uploaded = uploads.upload(file, "profile");
        var updated = users.updateProfileImage(authentication.getName(), uploaded.secureUrl());
        return ApiResponse.ok("Profile photo updated", safe(updated));
    }

    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<String> password(Authentication authentication, @Valid @RequestBody UserDtos.ChangePasswordRequest request) {
        users.changePassword(authentication.getName(), request);
        return ApiResponse.ok("Password changed", "Password changed successfully");
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<User>> all() {
        return ApiResponse.ok("Users", users.all().stream().map(this::safe).toList());
    }

    private User safe(User user) {
        user.setPassword(null);
        return user;
    }
}
