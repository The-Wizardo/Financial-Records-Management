package com.FRM.Exception;

public record ApiResponse<T>(
        int status,
        String message,
        T data
) {
}
