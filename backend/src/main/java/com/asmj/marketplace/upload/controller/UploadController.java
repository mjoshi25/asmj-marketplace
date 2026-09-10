package com.asmj.marketplace.upload.controller;

import com.asmj.marketplace.common.response.ApiResponse;
import com.asmj.marketplace.upload.dto.UploadResponse;
import com.asmj.marketplace.upload.service.CloudinaryUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class UploadController {
    private final CloudinaryUploadService service;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UploadResponse> upload(@RequestParam("file") MultipartFile file,
                                               @RequestParam(defaultValue="listing") String purpose) {
        return ApiResponse.ok("Uploaded", service.upload(file, purpose));
    }
}
