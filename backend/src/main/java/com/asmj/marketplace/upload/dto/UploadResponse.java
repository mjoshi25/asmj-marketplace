package com.asmj.marketplace.upload.dto;

public record UploadResponse(String publicId, String secureUrl, String resourceType, String fileName, String mediaType) {}
