package com.kosmos.catalog_service.aspect.product.dao.entity;

import com.kosmos.catalog_service.aspect.product.type.ProductStatus;
import com.kosmos.catalog_service.aspect.product.type.ShippingType;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity{
    
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    @ColumnDefault("1")
    private Integer stock;

    @Column(nullable = false, name = "product_status", columnDefinition = "ON_SALE")
    private ProductStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShippingType shippingType;
    @Column(nullable = false)
    private Integer shippingPrice;

    @Column(nullable = false)
    private Integer reservationPrice;

    @ColumnDefault("0")
    private Integer reservedStock;

    @OneToMany(mappedBy = "product")
    private List<Attachment> attachments = new ArrayList<Attachment>();

    @OneToOne(mappedBy = "product")
    private Position position;

    /*public int getTotal(Integer price, Integer stock, Integer shippingPrice, Integer reservationPrice, Integer reservedStock) {
        return (this.price * this.stock);
    }
    */
}
