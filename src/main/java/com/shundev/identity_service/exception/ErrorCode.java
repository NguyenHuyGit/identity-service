package com.shundev.identity_service.exception;

public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999,"  Uncategorized exception"),
    INVALID_KEY(1001, "Invalid message key"),
    USER_EXISTS(1002, "User already exists"),
    USER_INVALID(1003,"User is invalid"),
    PASSWORD_INVALID(1004,"Password is at least 8 characters."),
    USER_NOT_FOUND(1005,"User not found"),
    UNAUTHENTICATED(1006,"Unauthenticated"),
    ;

    ErrorCode(int code, String message){
        this.code = code;
        this.message = message;
    }
    
    private int code;
    private String message;
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
