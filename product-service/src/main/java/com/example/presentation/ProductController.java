package com.example.presentation;

import com.example.business.ProductRequestDto;
import com.example.business.ProductResponseDto;
import com.example.business.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;

    @PostMapping("api/product")
    @ResponseStatus(HttpStatus.CREATED)
    public void addProduct(@RequestBody ProductRequestDto productRequestDTO) {
        log.info("Valid request to add product received: " + productRequestDTO);
        productService.add(productRequestDTO);
    }

    @GetMapping("api/product")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponseDto> getAllProducts() {
        log.info("Valid request to get all products received");
        return productService.getAll();
    }
}
