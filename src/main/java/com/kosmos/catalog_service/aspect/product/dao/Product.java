package com.kosmos.catalog_service.aspect.product.dao;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(nullable = false, length = 50, name = "product_name")
    private String name;
    @Column(nullable = false)
    private Integer price;
    @Column(nullable = false, name = "seller_id")
    private Long sellerId;
    @Lob
    private String description;

    @Column(nullable = false, length = 20)
    private String categoryFirst;
    @Column(length = 20)
    private String categorySecond;
    @Column(length = 20)
    private String categoryThird;

    @Column(nullable = false)
    @ColumnDefault("1")
    private Integer stock;

//    @Enumerated(EnumType.String)
//    @Column(nullable = false)
//    private ShippingType shippingType;
    @Column(nullable = false)
    private Integer shippingPrice;

    @Column(nullable = false)
    private Integer reservationPrice;

    @Column(nullable = false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime timestamp;

    @ColumnDefault("0")
    private Integer reservedStock;
}
