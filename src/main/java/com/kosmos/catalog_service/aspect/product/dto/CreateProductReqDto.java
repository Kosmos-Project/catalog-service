package com.kosmos.catalog_service.aspect.product.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateProductReqDto {
    @PositiveOrZero(message = "valid.pet.id.notNegative")
    private Long petId;
    @Size(max = 10000, message = "valid.product.contents.size")
    private String contents;
    @Size(max = 5, message = "valid.product.hashTags.count")
    private List<@Size(max = 20, message = "valid.product.hashTags.size") String> hashTags;
    @Pattern(
            regexp = "^(PUBLIC|PRIVATE|FRIEND)$",
            message = "valid.product.disclosure.enum"
    )
    private String disclosure;
    @DecimalMax(value = "90.0", message = "valid.product.geoTagLat.max")
    @DecimalMin(value = "-90.0", message = "valid.product.geoTagLat.min")
    private BigDecimal geoTagLat;
    @DecimalMax(value = "180.0", message = "valid.product.geoTagLong.max")
    @DecimalMin(value = "-180.0", message = "valid.product.geoTagLong.min")
    private BigDecimal geoTagLong;
}
