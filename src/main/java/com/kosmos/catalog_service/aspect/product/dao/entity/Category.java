package com.kosmos.catalog_service.aspect.product.dao.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    @Id
    @Column(name="category_id")
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String categoryFirst;
    @Column(length = 20)
    private String categorySecond;
    @Column(length = 20)
    private String categoryThird;
}
