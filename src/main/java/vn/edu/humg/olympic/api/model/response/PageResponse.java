package vn.edu.humg.olympic.api.model.response;

import java.util.List;
import lombok.Builder;

@Builder
public record PageResponse<T>(
        List<T> items,
        int page,
        int size,
        long totalItems,
        int totalPages
) {}
