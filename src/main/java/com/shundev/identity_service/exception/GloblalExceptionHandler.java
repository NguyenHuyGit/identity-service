package com.shundev.identity_service.exception;

import java.text.ParseException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.shundev.identity_service.dto.request.ApiResponse;

@ControllerAdvice
public class GloblalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingException(RuntimeException exception){
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode()); 
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    // Handle exception when validate fail 
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingMethodArgumentNotValidException(MethodArgumentNotValidException exception){
        String enumKey = exception.getFieldError().getDefaultMessage();

        ErrorCode errorCode = ErrorCode.INVALID_KEY;

        try{
            errorCode = ErrorCode.valueOf(enumKey);
        }
        catch(IllegalArgumentException e){
            
        }
        
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        
        return ResponseEntity.badRequest().body(apiResponse);
    }
    
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handldingMethodAppException(AppException exception){
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(exception.getErrorCode().getCode());
        apiResponse.setMessage(exception.getErrorCode().getMessage());
        return ResponseEntity.badRequest().body(apiResponse);  
    }

    @ExceptionHandler(value = ParseException.class)
    ResponseEntity<ApiResponse> handldingParseException(ParseException exception){
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(1007);
        apiResponse.setMessage("Parse Exception");
        return ResponseEntity.badRequest().body(apiResponse);  
    }
} 