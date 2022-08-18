package com.kosmos.catalog_service.aspect.product.dto;

import lombok.Data;

import javax.validation.constraints.PositiveOrZero;

@Data
public class FetchProductVideoReqDto {
    @PositiveOrZero
    Long id;
    @PositiveOrZero
    Integer index;
}
