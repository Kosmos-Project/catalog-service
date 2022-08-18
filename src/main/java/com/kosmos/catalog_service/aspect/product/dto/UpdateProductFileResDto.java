package com.kosmos.catalog_service.aspect.product.dto;

import com.kosmos.catalog_service.common.http.DtoMetadata;
import com.kosmos.catalog_service.common.storage.FileMetadata;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UpdateProductFileResDto {
    private DtoMetadata _metadata;
    private List<FileMetadata> fileMetadataList;
}
