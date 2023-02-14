package com.example.business.events;

import com.example.business.dtos.OrderLineItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderEvent {
    private String orderNumber;
    private List<OrderLineItemDto> orderLineItems;
}
