package com.kosmos.catalog_service.common.storage;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileMetadata {
    private String name;
    private Long size;
    private String entity;
    private String type;
    private String url;
}
