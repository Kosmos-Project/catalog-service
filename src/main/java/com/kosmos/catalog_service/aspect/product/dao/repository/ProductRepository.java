package com.kosmos.catalog_service.aspect.product.dao.repository;

import com.kosmos.catalog_service.aspect.product.dao.entity.Category;
import com.kosmos.catalog_service.aspect.product.dao.entity.Product;
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

    List<Product> findByCategory(Category category);

}
