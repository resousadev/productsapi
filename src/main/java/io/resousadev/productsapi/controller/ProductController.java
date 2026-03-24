package io.resousadev.productsapi.controller;

import io.resousadev.productsapi.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @PostMapping("/save")
    public Product saveProduct(@RequestBody Product product) {
        log.info("Saving product: {}", product);
        return product;
    }

}
