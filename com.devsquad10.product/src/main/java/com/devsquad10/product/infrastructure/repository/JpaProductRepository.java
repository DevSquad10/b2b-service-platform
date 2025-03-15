package com.devsquad10.product.infrastructure.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsquad10.product.domain.model.Product;
import com.devsquad10.product.domain.repository.ProductRepository;

@Repository
public interface JpaProductRepository extends JpaRepository<Product, UUID>, ProductRepository {

}
