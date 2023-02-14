package com.example.business.dtos;

import jakarta.persistence.ElementCollection;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {
    private Long customerId;
    @NotEmpty
    @ElementCollection
    private List<OrderLineItemDto> orderLineItemDtoList;
}
