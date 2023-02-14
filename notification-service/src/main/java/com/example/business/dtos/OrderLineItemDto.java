package com.example.business.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineItemDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer quantity;
}
