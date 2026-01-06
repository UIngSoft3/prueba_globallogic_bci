package com.globallogic.bci.dto;

import java.util.List;

/**
 * Data Transfer Object for error response wrapper.
 * Wraps error details in a list for consistent error response format.
 */
public class ErrorResponse {
    private List<ErrorDetail> error;

    public ErrorResponse() {
    }

    public ErrorResponse(List<ErrorDetail> error) {
        this.error = error;
    }

    public List<ErrorDetail> getError() {
        return error;
    }

    public void setError(List<ErrorDetail> error) {
        this.error = error;
    }
}
