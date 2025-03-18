package com.devsquad10.product.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.devsquad10.product.domain.model.Product;

public interface ProductRepository {

	Optional<Product> findByIdAndDeletedAtIsNull(UUID id);

	Product save(Product product);

	Page<Product> findAll(String q, String category, int page, int size, String sort, String order);

	int decreaseStock(UUID productId, int orderQuantity);
}
