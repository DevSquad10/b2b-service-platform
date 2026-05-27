package com.devsquad10.product.infrastructure.repository;

import com.devsquad10.product.domain.model.Product;
import com.devsquad10.product.domain.repository.ProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaProductRepository
        extends JpaRepository<Product, UUID>, QuerydslPredicateExecutor<Product>, ProductRepository {
}
