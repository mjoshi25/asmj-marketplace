package com.asmj.marketplace.auth.dto;

import com.asmj.marketplace.user.model.User;
import jakarta.validation.constraints.*;

public final class AuthDtos {
    private AuthDtos() {}
    public record RegisterRequest(
        @NotBlank String name,
        @Email @NotBlank String email,
        @NotBlank @Size(min=10,max=15) String contactNumber,
        @NotBlank @Size(min=6) String password,
        @NotNull User.Role role
    ) {}
    public record LoginRequest(@Email @NotBlank String email,@NotBlank String password) {}
    public record AuthResponse(String token,String refreshToken,String id,String name,String email,String contactNumber,java.util.Set<User.Role> roles,String profileImage) {}
}
