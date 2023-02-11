package com.example.business;

import com.example.persistence.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    public void add(ProductRequestDto productRequestDTO) {
        Product product = Product.builder()
                .name(productRequestDTO.getName())
                .description(productRequestDTO.getDescription())
                .price(productRequestDTO.getPrice())
                .build();

        productRepository.save(product);
        log.info("New product added: {}.", product);
    }

    public List<ProductResponseDto> getAll() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(this::mapToProductResponseDTO).toList();
    }

    private ProductResponseDto mapToProductResponseDTO(Product product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
    }
}
