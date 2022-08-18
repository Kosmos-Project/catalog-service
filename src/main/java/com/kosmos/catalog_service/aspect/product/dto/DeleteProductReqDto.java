package com.kosmos.catalog_service.aspect.product.dto;

import lombok.Data;

import javax.validation.constraints.PositiveOrZero;

@Data
public class DeleteProductReqDto {
    @PositiveOrZero(message = "valid.product.id.notNegative")
    private Long id;
}
