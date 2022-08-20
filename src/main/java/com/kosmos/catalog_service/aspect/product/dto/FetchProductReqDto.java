package com.kosmos.catalog_service.aspect.product.dto;

import lombok.Data;

import javax.validation.constraints.PositiveOrZero;

@Data
public class FetchProductReqDto {
    @PositiveOrZero(message = "valid.product.pageIndex.notNegative")
    private Integer pageIndex;
    @PositiveOrZero(message = "valid.pet.id.notNegative")
    private Long petId;
    @PositiveOrZero(message = "valid.product.id.notNegative")
    private Long id;
    @PositiveOrZero(message = "valid.product.id.notNegative")
    private Long topProductId;
}
