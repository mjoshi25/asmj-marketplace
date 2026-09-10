package com.asmj.marketplace.post.controller;

import com.asmj.marketplace.post.dto.PostRequest;
import com.asmj.marketplace.post.model.Post;
import com.asmj.marketplace.post.service.PostService;
import com.asmj.marketplace.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService s;

    @GetMapping
    public ApiResponse<List<Post>> all(@RequestParam(required=false) String type, @RequestParam(required=false) String q) {
        if (q != null && !q.isBlank()) return ApiResponse.ok("Search results", s.search(q, type));
        return ApiResponse.ok("Posts", s.all(type));
    }

    @GetMapping("/search")
    public ApiResponse<List<Post>> search(@RequestParam String q, @RequestParam(required=false) String type) {
        return ApiResponse.ok("Search results", s.search(q, type));
    }

    @GetMapping("/{id}/preview")
    public ApiResponse<Post> preview(@PathVariable String id){return ApiResponse.ok("Post preview",s.preview(id));}

    @GetMapping("/{id}")
    public ApiResponse<Post> get(@PathVariable String id){return ApiResponse.ok("Post",s.get(id));}

    @PostMapping
    @PreAuthorize("hasRole('VENDOR')")
    public ApiResponse<Post> create(Authentication a,@Valid @RequestBody PostRequest r){return ApiResponse.ok("Created",s.create(a.getName(),r));}

    @GetMapping("/vendor/mine")
    @PreAuthorize("hasRole('VENDOR')")
    public ApiResponse<List<Post>> mine(Authentication a){return ApiResponse.ok("My posts",s.mine(a.getName()));}

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('VENDOR')")
    public ApiResponse<Post> update(Authentication a,@PathVariable String id,@Valid @RequestBody PostRequest r){return ApiResponse.ok("Updated",s.update(a.getName(),id,r));}

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('VENDOR')")
    public ApiResponse<Void> delete(Authentication a,@PathVariable String id){s.delete(a.getName(),id);return ApiResponse.ok("Deleted",null);}

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasRole('VENDOR')")
    public ApiResponse<Post> submit(Authentication a,@PathVariable String id){return ApiResponse.ok("Submitted",s.submit(a.getName(),id));}
}
