package io.resousadev.productsapi.controller;

import io.resousadev.productsapi.model.Product;
import io.resousadev.productsapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;

    @PostMapping("/save")
    public Product saveProduct(@RequestBody Product product) {
        log.info("Saving product: {}", product);
        Product savedProduct = productRepository.save(product);
        log.info("Product saved with ID: {}", savedProduct.getId());
        return savedProduct;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        log.info("Fetching all products");
        return productRepository.findAll();
    }

}
