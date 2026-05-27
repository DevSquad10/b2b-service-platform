package com.devsquad10.product.domain.repository;

import com.devsquad10.product.domain.model.Product;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    Optional<Product> findByIdAndDeletedAtIsNull(UUID id);

    Product save(Product product);
}
