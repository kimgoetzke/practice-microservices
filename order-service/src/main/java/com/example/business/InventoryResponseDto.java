package com.example.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryResponseDto {
    private String skuCode;
    private Integer quantity;
    private boolean isInStock;
}
