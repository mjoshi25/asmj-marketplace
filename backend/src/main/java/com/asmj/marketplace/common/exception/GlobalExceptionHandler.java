package com.asmj.marketplace.common.exception;
import com.asmj.marketplace.common.response.ApiResponse;
import org.springframework.http.*; import org.springframework.web.bind.MethodArgumentNotValidException; import org.springframework.web.bind.annotation.*;
@RestControllerAdvice public class GlobalExceptionHandler {
 @ExceptionHandler(ResourceNotFoundException.class) ResponseEntity<ApiResponse<Void>> notFound(ResourceNotFoundException e){return ResponseEntity.status(404).body(ApiResponse.error(e.getMessage()));}
 @ExceptionHandler(IllegalArgumentException.class) ResponseEntity<ApiResponse<Void>> bad(IllegalArgumentException e){return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ApiResponse<Void>> validation(MethodArgumentNotValidException e){return ResponseEntity.badRequest().body(ApiResponse.error(e.getBindingResult().getFieldErrors().stream().map(x->x.getField()+": "+x.getDefaultMessage()).findFirst().orElse("Validation failed")));}
 @ExceptionHandler(Exception.class) ResponseEntity<ApiResponse<Void>> generic(Exception e){return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage()==null?"Internal server error":e.getMessage()));}
}
