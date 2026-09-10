package com.asmj.marketplace.upload.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.asmj.marketplace.upload.dto.UploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryUploadService {
    private final Cloudinary cloudinary;

    public UploadResponse upload(MultipartFile file, String purpose) {
        try {
            if (file == null || file.isEmpty()) throw new IllegalArgumentException("File is empty");
            String safePurpose = purpose == null || purpose.isBlank() ? "listing" : purpose.replaceAll("[^a-zA-Z0-9_-]", "_");
            Map<?,?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "resource_type", "auto",
                "folder", "asmj-marketplace/" + safePurpose
            ));
            return new UploadResponse(
                String.valueOf(result.get("public_id")),
                String.valueOf(result.get("secure_url")),
                String.valueOf(result.get("resource_type")),
                file.getOriginalFilename(),
                file.getContentType()
            );
        } catch (Exception e) {
            throw new IllegalArgumentException("Cloudinary upload failed: " + e.getMessage(), e);
        }
    }
}
