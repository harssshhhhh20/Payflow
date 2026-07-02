package com.harsh.payflow.common.response;

import java.time.Instant;
import java.util.List;

public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        List<String> errors,
        Instant timestamp
) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
                true,
                "Success",
                data,
                List.of(),
                Instant.now()
        );
    }

    public static <T> ApiResponse<T> success(
            String message,
            T data
    ) {
        return new ApiResponse<>(
                true,
                message,
                data,
                List.of(),
                Instant.now()
        );
    }

    public static ApiResponse<Void> success(
            String message
    ) {
        return new ApiResponse<>(
                true,
                message,
                null,
                List.of(),
                Instant.now()
        );
    }

    public static ApiResponse<Void> failure(
            String message
    ) {
        return new ApiResponse<>(
                false,
                message,
                null,
                List.of(),
                Instant.now()
        );
    }

    public static ApiResponse<Void> failure(
            String message,
            List<String> errors
    ) {
        return new ApiResponse<>(
                false,
                message,
                null,
                errors,
                Instant.now()
        );
    }
}