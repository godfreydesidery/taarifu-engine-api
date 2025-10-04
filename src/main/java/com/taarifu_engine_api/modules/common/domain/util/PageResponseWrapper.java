package com.taarifu_engine_api.modules.common.domain.util;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic wrapper for paginated API responses.
 * <p>
 * Provides a consistent response structure across the API
 * while including pagination metadata. This helps mobile and
 * web clients handle paginated results without parsing raw
 * Spring Data {@link Page} objects.
 *
 * @param <T> the type of elements in the paginated response
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResponseWrapper<T> {

    /**
     * Indicates whether the request was processed successfully.
     * {@code true} = success, {@code false} = error.
     */
    private boolean status;

    /**
     * HTTP status code (e.g., 200, 400, 500).
     */
    private int statusCode;

    /**
     * Human-readable description of the response outcome.
     * Example: "Users fetched successfully".
     */
    private String message;

    /**
     * The actual content of the current page.
     */
    private List<T> data;

    /**
     * Zero-based index of the current page.
     */
    private int pageNumber;

    /**
     * Number of items per page.
     */
    private int pageSize;

    /**
     * Total number of elements across all pages.
     */
    private long totalElements;

    /**
     * Total number of available pages.
     */
    private int totalPages;

    /**
     * Flag indicating if this is the last page.
     */
    private boolean last;
    
    /**
     * Timestamp when the response was generated (UTC).
     */
    private Instant timestamp;

    /**
     * Factory method to create a {@link PageResponseWrapper} from a Spring {@link Page}.
     *
     * @param page    the Page object containing paginated data
     * @param message descriptive message for the response
     * @param <T>     the type of elements in the page
     * @return standardized {@link PageResponseWrapper} with pagination metadata
     */
    public static <T> PageResponseWrapper<T> fromPage(Page<T> page, String message) {
        return new PageResponseWrapper<>(
                true,
                200,
                message,
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast(),
                Instant.now());
    }
}
