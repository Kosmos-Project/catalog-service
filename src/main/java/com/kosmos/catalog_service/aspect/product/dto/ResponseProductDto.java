package com.kosmos.catalog_service.aspect.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseProductDto {

    private String name;
    private Integer price;
    private Long sellerId;
    private String description;

    private String categoryFirst;
    private String categorySecond;
    private String categoryThird;

    private Integer stock;
    private Integer shippingPrice;
    private Integer reservationPrice;
    private LocalDateTime timestamp;
    private Integer reservedStock;
}
