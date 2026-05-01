package com.example.domitory.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer code;
    private String message;
    private T data;
    private Long count;
    
    public static <T> Result<T> success() {
        return success(null, null);
    }
    
    public static <T> Result<T> success(T data) {
        return success(data, null);
    }
    
    public static <T> Result<T> success(T data, Long count) {
        Result<T> result = new Result<>();
        result.setCode(0);
        result.setMessage("操作成功");
        result.setData(data);
        result.setCount(count);
        return result;
    }
    
    public static <T> Result<T> error(String message) {
        return error(500, message);
    }
    
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
    
    public static <T> Result<T> page(T data, Long count) {
        Result<T> result = new Result<>();
        result.setCode(0);
        result.setMessage("获取成功");
        result.setData(data);
        result.setCount(count);
        return result;
    }
}
