package com.kosmos.catalog_service.aspect.product.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findById(Long id);
    Optional<Product> findByName(String name);

    List<Product> findBySellerId(Long sellerId);

    List<Product> findByCategoryFirst(String categoryFirst);

    @Query("select p from Product p where p.categoryFirst = ?1 and p.categorySecond = ?2")
    List<Product> findByCategoryFirstAndCategorySecond(String categoryFirst, String categorySecond);

    @Query("select p from Product p where p.categoryFirst = ?1 and p.categorySecond = ?2 and p.categoryThird = ?3")
    List<Product> findByCategoryFirstAndCategorySecondAndCategoryThird(String categoryFirst, String categorySecond, String categoryThird);

}
