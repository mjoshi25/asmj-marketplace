package com.asmj.marketplace.post.dto;

import jakarta.validation.constraints.*;
import com.asmj.marketplace.post.model.Post;
import java.util.*;

public record PostRequest(
    @NotBlank String categoryId,
    @NotBlank String type,
    @NotBlank String title,
    @NotBlank String description,
    Double price,
    String priceType,
    List<String> images,
    List<Post.MediaItem> attachments,
    Post.Location location,
    Map<String,Object> attributes,
    boolean featured
) {}
