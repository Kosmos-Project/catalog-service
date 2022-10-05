package com.kosmos.catalog_service.aspect.product.dto.product;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProductDto implements Serializable {

    private Integer price;
    private Long sellerId;
    private String description;

    private String categoryFirst;
    private String categorySecond;
    private String categoryThird;

    private Integer stock;
    private Integer shippingPrice;
    private Integer reservationPrice;

    private Integer reservedStock;

    private Integer totalPrice;

    private String orderId;
    private String userId;

}
