package com.stoikal.saktimart.common.dto;

public record ApiEnvelope<T>(
        T data, String message) {
    public static <T> ApiEnvelope<T> success(T data) {
        return new ApiEnvelope<>(data, null);
    }

    public static ApiEnvelope<Void> success() {
        return new ApiEnvelope<>(null, null);
    }

    public static <T> ApiEnvelope<T> error(String message) {
        return new ApiEnvelope<>(null, message);
    }
}
