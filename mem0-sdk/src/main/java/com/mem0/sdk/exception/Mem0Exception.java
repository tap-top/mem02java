package com.mem0.sdk.exception;

/**
 * Mem0 SDK 异常类
 * 
 * @author changyu496
 */
public class Mem0Exception extends Exception {
    
    /**
     * 错误代码
     */
    private final String errorCode;
    
    /**
     * HTTP 状态码
     */
    private final Integer httpStatus;
    
    /**
     * 构造函数
     * 
     * @param message 错误消息
     */
    public Mem0Exception(String message) {
        super(message);
        this.errorCode = "UNKNOWN_ERROR";
        this.httpStatus = 500;
    }
    
    /**
     * 构造函数
     * 
     * @param message 错误消息
     * @param cause 原因
     */
    public Mem0Exception(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "UNKNOWN_ERROR";
        this.httpStatus = 500;
    }
    
    /**
     * 构造函数
     * 
     * @param errorCode 错误代码
     * @param message 错误消息
     * @param httpStatus HTTP 状态码
     */
    public Mem0Exception(String errorCode, String message, Integer httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
    
    /**
     * 构造函数
     * 
     * @param errorCode 错误代码
     * @param message 错误消息
     * @param httpStatus HTTP 状态码
     * @param cause 原因
     */
    public Mem0Exception(String errorCode, String message, Integer httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
    
    /**
     * 获取错误代码
     * 
     * @return 错误代码
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * 获取 HTTP 状态码
     * 
     * @return HTTP 状态码
     */
    public Integer getHttpStatus() {
        return httpStatus;
    }
    
    /**
     * 创建网络异常
     * 
     * @param message 错误消息
     * @param cause 原因
     * @return 网络异常
     */
    public static Mem0Exception networkError(String message, Throwable cause) {
        return new Mem0Exception("NETWORK_ERROR", message, 0, cause);
    }
    
    /**
     * 创建认证异常
     * 
     * @param message 错误消息
     * @return 认证异常
     */
    public static Mem0Exception authenticationError(String message) {
        return new Mem0Exception("AUTHENTICATION_ERROR", message, 401);
    }
    
    /**
     * 创建授权异常
     * 
     * @param message 错误消息
     * @return 授权异常
     */
    public static Mem0Exception authorizationError(String message) {
        return new Mem0Exception("AUTHORIZATION_ERROR", message, 403);
    }
    
    /**
     * 创建参数异常
     * 
     * @param message 错误消息
     * @return 参数异常
     */
    public static Mem0Exception parameterError(String message) {
        return new Mem0Exception("PARAMETER_ERROR", message, 400);
    }
    
    /**
     * 创建资源不存在异常
     * 
     * @param message 错误消息
     * @return 资源不存在异常
     */
    public static Mem0Exception notFoundError(String message) {
        return new Mem0Exception("NOT_FOUND_ERROR", message, 404);
    }
    
    /**
     * 创建服务器异常
     * 
     * @param message 错误消息
     * @return 服务器异常
     */
    public static Mem0Exception serverError(String message) {
        return new Mem0Exception("SERVER_ERROR", message, 500);
    }
    
    /**
     * 创建服务器异常
     * 
     * @param message 错误消息
     * @param cause 原因
     * @return 服务器异常
     */
    public static Mem0Exception serverError(String message, Throwable cause) {
        return new Mem0Exception("SERVER_ERROR", message, 500, cause);
    }
} 