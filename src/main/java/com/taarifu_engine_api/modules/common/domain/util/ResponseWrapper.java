package com.taarifu_engine_api.modules.common.domain.util;

import java.time.Instant;

import lombok.Data;

/**
 * A generic wrapper for HTTP API responses.
 * Used to standardize the structure of success and error responses
 * throughout the REST API.
 *
 * @param <T> the type of data included in the response body
 */
@Data
public class ResponseWrapper<T> {
    /**
     * Indicates whether the operation was successful or not.
     * true for success, false for error/failure.
     */
    private boolean status;
    /**
     * The HTTP status code (e.g. 200, 404, 500).
     */
    private int statusCode;
    /**
     * A human-readable message explaining the response.
     * Could be "Success", "Validation failed", etc.
     */
    private String message;
     /**
     * The actual payload/data of the response.
     * Can be an object, list, or null depending on the endpoint.
     */
    private T data;

    /**
     * The server timestamp when the response was created.
     * Useful for debugging, logging, and syncing client time.
     */
    private Instant timestamp;

    /**
     * Constructs a new standardized response wrapper.
     *
     * @param status     whether the response indicates success
     * @param statusCode the HTTP status code
     * @param message    descriptive message for the response
     * @param data       the response body payload
     */
    public ResponseWrapper(boolean status, int statusCode, String message, T data) {
        this.status = status;
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
        this.timestamp = Instant.now(); // auto-assign timestamp
    }
}
