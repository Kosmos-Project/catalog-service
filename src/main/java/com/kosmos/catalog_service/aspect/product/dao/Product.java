package com.kosmos.catalog_service.aspect.product.dao;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    
    //FIXME: 아래 코드는 CRUD 구현을 위한 참고용 예제 코드입니다
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    /*
    아래는 참고용 예시이나 타 프로젝트에서 가져온 예시 코드인 관계로 의존하는 VO가 없어 주석처리
    @ManyToOne(targetEntity = Account.class, fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "account_id")
    private Account author;

    @ManyToOne(targetEntity = Pet.class, fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "pet_id")
    private Pet pet;
     */

    @Lob
    @Column
    private String contents;
    @Column(nullable = false)
    private LocalDateTime timestamp;
    @Column(nullable = false)
    private Boolean edited;

    @Column(name = "tag_list")
    private String serializedHashTags;

    @Column
    private String disclosure;
    private Double geoTagLat;
    private Double geoTagLong;
    @Lob
    private String imageAttachments;
    @Lob
    private String videoAttachments;
    @Lob
    private String fileAttachments;
}
