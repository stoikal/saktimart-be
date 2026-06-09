package com.stoikal.saktimart.common.dto;

import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

// DTO untuk menyimpan parameter pagination yang sudah dinormalisasi.
// Gunakan static factory PageableRequest.of() agar normalisasi terpusat.
// Record ini hanya menyimpan nilai final yang sudah bersih.
public record PageableRequest(
        int page,
        int size,
        String sort,
        String direction) {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 999999;
    private static final String DEFAULT_DIRECTION = "asc";
    private static final Set<String> ALLOWED_DIRECTIONS = Set.of("asc", "desc");

    public Pageable toPageable() {
        if (sort == null || sort.isBlank()) {
            return PageRequest.of(page - 1, size);
        }

        Sort.Direction dir = Sort.Direction.ASC;
        if (direction != null && !direction.isBlank()) {
            dir = Sort.Direction.fromString(direction);
        }

        return PageRequest.of(page - 1, size, Sort.by(dir, sort));
    }

    public static PageableRequest of(
            Integer page,
            Integer size,
            String sort,
            String direction,
            String defaultSort,
            Set<String> allowedSorts) {
        int effectivePage = (page == null || page < 1) ? DEFAULT_PAGE : page;
        int effectiveSize = (size == null || size < 1) ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        String effectiveSort = (sort == null || sort.isBlank() || !allowedSorts.contains(sort))
                ? defaultSort
                : sort;
        String effectiveDir = (direction == null || direction.isBlank()
                || !ALLOWED_DIRECTIONS.contains(direction.toLowerCase()))
                        ? DEFAULT_DIRECTION
                        : direction;

        return new PageableRequest(effectivePage, effectiveSize, effectiveSort, effectiveDir);
    }
}
