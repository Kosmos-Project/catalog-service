package com.kosmos.catalog_service.aspect.product.dto;

import com.kosmos.catalog_service.aspect.product.dao.Product;
import com.kosmos.catalog_service.common.http.DtoMetadata;
import lombok.Data;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Data
public class FetchProductResDto {
    private DtoMetadata _metadata;
    private List<Product> productList;
    private Pageable pageable;
    private Boolean isLast;
    
    // 정상 조회시 사용할 생성자
    public FetchProductResDto(DtoMetadata dtoMetadata, List<Product> productList, Pageable pageable, Boolean isLast) {
        this._metadata = dtoMetadata;
        this.productList = productList;
        this.pageable = pageable;
        this.isLast = isLast;
    }
    
    // 오류시 사용할 생성자
    public FetchProductResDto(DtoMetadata dtoMetadata) {
        this._metadata = dtoMetadata;
        this.productList = null;
        this.pageable = null;
        this.isLast = null;
    }
}
