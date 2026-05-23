package com.stoikal.saktimart.common.dto;

import java.util.List;

public record PaginatedResponse<T>(
        List<T> elements,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages) {
}
