package com.kosmos.catalog_service.aspect.product.dto;

import com.kosmos.catalog_service.common.http.DtoMetadata;
import lombok.Data;

@Data
public class CreateProductResDto {
    private DtoMetadata _metadata;
    private Long id;

    // 정상 조회시 사용할 생성자
    public CreateProductResDto(DtoMetadata dtoMetadata, Long productId) {
        this._metadata = dtoMetadata;
        this.id = productId;
    }

    // 오류시 사용할 생성자
    public CreateProductResDto(DtoMetadata dtoMetadata) {
        this._metadata = dtoMetadata;
        this.id = null;
    }
}
