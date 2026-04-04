package io.resousadev.productsapi.repository;

import io.resousadev.productsapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}
