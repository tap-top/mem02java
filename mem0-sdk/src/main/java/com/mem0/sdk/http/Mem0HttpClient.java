package com.mem0.sdk.http;

import com.mem0.sdk.config.Mem0Config;
import com.mem0.sdk.exception.Mem0Exception;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Mem0 HTTP 客户端
 * 
 * @author changyu496
 */
@Slf4j
public class Mem0HttpClient {
    
    private final Mem0Config config;
    private final java.net.http.HttpClient httpClient;
    
    /**
     * 构造函数
     * 
     * @param config SDK 配置
     */
    public Mem0HttpClient(Mem0Config config) {
        this.config = config;
        this.httpClient = java.net.http.HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(config.getConnectTimeout()))
                .build();
    }
    
    /**
     * 发送 GET 请求
     * 
     * @param url 请求URL
     * @return 响应内容
     * @throws Mem0Exception 请求异常
     */
    public String get(String url) throws Mem0Exception {
        return executeRequest("GET", url, null);
    }
    
    /**
     * 发送 POST 请求
     * 
     * @param url 请求URL
     * @param body 请求体
     * @return 响应内容
     * @throws Mem0Exception 请求异常
     */
    public String post(String url, String body) throws Mem0Exception {
        return executeRequest("POST", url, body);
    }
    
    /**
     * 发送 PUT 请求
     * 
     * @param url 请求URL
     * @param body 请求体
     * @return 响应内容
     * @throws Mem0Exception 请求异常
     */
    public String put(String url, String body) throws Mem0Exception {
        return executeRequest("PUT", url, body);
    }
    
    /**
     * 发送 DELETE 请求
     * 
     * @param url 请求URL
     * @throws Mem0Exception 请求异常
     */
    public void delete(String url) throws Mem0Exception {
        executeRequest("DELETE", url, null);
    }
    
    /**
     * 执行 HTTP 请求
     * 
     * @param method HTTP 方法
     * @param url 请求URL
     * @param message 请求体
     * @return 响应内容
     * @throws Mem0Exception 请求异常
     */
    private String executeRequest(String method, String url, String message) throws Mem0Exception {
        int retryCount = 0;
        Exception lastException = null;
        
        while (retryCount <= (config.isEnableRetry() ? config.getMaxRetries() : 0)) {
            try {
                HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofMillis(config.getReadTimeout()))
                        .header("User-Agent", config.getUserAgent())
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json");
                
                // 根据方法设置请求体
                switch (method.toUpperCase()) {
                    case "GET":
                        requestBuilder.GET();
                        break;
                    case "POST":
                        requestBuilder.POST(HttpRequest.BodyPublishers.ofString(message != null ? message : ""));
                        break;
                    case "PUT":
                        requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(message != null ? message : ""));
                        break;
                    case "DELETE":
                        requestBuilder.DELETE();
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported HTTP method: " + method);
                }
                
                HttpRequest request = requestBuilder.build();
                
                if (config.isEnableLogging()) {
                    log.debug("Sending {} request to: {}", method, url);
                    if (message != null) {
                        log.debug("Request body: {}", message);
                    }
                }
                
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                
                if (config.isEnableLogging()) {
                    log.debug("Response status: {}", response.statusCode());
                    log.debug("Response body: {}", response.body());
                }
                
                // 检查响应状态码
                if (response.statusCode() >= 200 && response.statusCode() < 300) {
                    return response.body();
                } else {
                    String errorMessage = String.format("HTTP %d: %s", response.statusCode(), response.body());
                    
                    if (response.statusCode() == 401) {
                        throw Mem0Exception.authenticationError(errorMessage);
                    } else if (response.statusCode() == 403) {
                        throw Mem0Exception.authorizationError(errorMessage);
                    } else if (response.statusCode() == 404) {
                        throw Mem0Exception.notFoundError(errorMessage);
                    } else if (response.statusCode() >= 400 && response.statusCode() < 500) {
                        throw Mem0Exception.parameterError(errorMessage);
                    } else {
                        throw Mem0Exception.serverError(errorMessage);
                    }
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw Mem0Exception.networkError("Request interrupted", e);
            } catch (IOException e) {
                lastException = e;
                retryCount++;
                
                if (retryCount <= (config.isEnableRetry() ? config.getMaxRetries() : 0)) {
                    log.warn("Request failed, retrying {}/{}: {}", retryCount, config.getMaxRetries(), e.getMessage());
                    
                    try {
                        Thread.sleep(config.getRetryInterval());
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw Mem0Exception.networkError("Retry interrupted", ie);
                    }
                } else {
                    log.error("Request failed after {} retries", config.getMaxRetries(), e);
                    throw Mem0Exception.networkError("Request failed after retries", e);
                }
            } catch (Mem0Exception e) {
                // 直接重新抛出 Mem0Exception
                throw e;
            } catch (Exception e) {
                lastException = e;
                retryCount++;
                
                if (retryCount <= (config.isEnableRetry() ? config.getMaxRetries() : 0)) {
                    log.warn("Request failed, retrying {}/{}: {}", retryCount, config.getMaxRetries(), e.getMessage());
                    
                    try {
                        Thread.sleep(config.getRetryInterval());
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw Mem0Exception.networkError("Retry interrupted", ie);
                    }
                } else {
                    log.error("Request failed after {} retries", config.getMaxRetries(), e);
                    throw Mem0Exception.serverError("Request failed after retries", e);
                }
            }
        }
        
        // 这里不应该到达，但为了编译安全
        throw Mem0Exception.serverError("Request failed", lastException);
    }
} 