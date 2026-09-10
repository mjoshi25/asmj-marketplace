package com.asmj.marketplace.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class UserDtos {
    private UserDtos() {}
    public record UpdateProfileRequest(@NotBlank String name,@NotBlank @Size(min=10,max=15) String mobile,String profileImage) {}
    public record ChangePasswordRequest(@NotBlank String currentPassword,@NotBlank @Size(min=6) String newPassword) {}
}
