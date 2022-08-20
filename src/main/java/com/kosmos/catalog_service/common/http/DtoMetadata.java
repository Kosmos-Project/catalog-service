package com.kosmos.catalog_service.common.http;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DtoMetadata {
    private Boolean status;
    private String message;
    private String exception;

    public DtoMetadata(String message) {
        this.status = true;
        this.message = message;
        this.exception = null;
    }

    public DtoMetadata(String message, String exception) {
        this.status = false;
        this.message = message;
        this.exception = exception;
    }
}
