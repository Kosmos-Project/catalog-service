package com.kosmos.catalog_service.aspect.product.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /*
    아래 코드는 CRUD 구현 참고용 예시입니다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Product> findByAuthorAndId(Account author, Long id);
     */

    Optional<Product> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Product> findByAuthorAndId(Object author, Long id);

    @Query(
            value = "SELECT * FROM post AS p WHERE p.pet_id=:petId",
            countQuery = "SELECT COUNT(*) FROM post AS p WHERE p.pet_id=:petId",
            nativeQuery = true
    )
    Page<Product> findAllByTaggedPetId(@Param("petId") Long taggedPetId, Pageable pageable);

    @Query(
            value = "SELECT * FROM post AS p WHERE p.disclosure=\"PUBLIC\" OR (p.disclosure=\"FRIEND\" AND p.account_id IN :friends) OR (p.disclosure=\"PRIVATE\" AND p.account_id=:me)",
            countQuery = "SELECT COUNT(*) FROM post AS p WHERE p.disclosure=\"PUBLIC\" OR (p.disclosure=\"FRIEND\" AND p.account_id IN :friends) OR (p.disclosure=\"PRIVATE\" AND p.account_id=:me)",
            nativeQuery = true
    )
    Page<Product> findAllByDefaultOption(
            @Param("friends") Collection<Long> friendAccountIdList,
            @Param("me") Long myAccountId,
            Pageable pageable
    );

    @Query(
            value = "SELECT * FROM post AS p WHERE p.post_id <= :top AND (p.disclosure=\"PUBLIC\" OR (p.disclosure=\"FRIEND\" AND p.account_id IN :friends) OR (p.disclosure=\"PRIVATE\" AND p.account_id=:me))",
            countQuery = "SELECT COUNT(*) FROM post AS p WHERE p.post_id <= :top AND (p.disclosure=\"PUBLIC\" OR (p.disclosure=\"FRIEND\" AND p.account_id IN :friends) OR (p.disclosure=\"PRIVATE\" AND p.account_id=:me))",
            nativeQuery = true
    )
    Page<Product> findAllByDefaultOptionAndTopProductId(
            @Param("top") Long topProductId,
            @Param("friends") Collection<Long> friendAccountIdList,
            @Param("me") Long myAccountId,
            Pageable pageable
    );
}
