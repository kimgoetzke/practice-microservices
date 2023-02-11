package com.example.business;

import com.example.persistence.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderResponseDto placeOrder(OrderRequestDto orderRequestDto) throws JsonProcessingException {
        List<InventoryRequestDto> requestList = new ArrayList<>();
        for (OrderRequestLineItemsDto lineItem : orderRequestDto.getOrderLineItemsDtoList()) {
            InventoryRequestDto inventoryRequestDto = InventoryRequestDto.builder()
                    .skuCode(lineItem.getSkuCode())
                    .quantity(lineItem.getQuantity())
                    .build();
            requestList.add(inventoryRequestDto);
        }

        String json = objectMapper.writeValueAsString(requestList);
        log.info("The following request will be submitted: " + json + ".");

        InventoryResponseDto[] inventoryResponseArray = webClientBuilder.build().post()
                .uri("http://inventory-service/api/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .retrieve()
                .bodyToMono(InventoryResponseDto[].class)
                .block();

        if (inventoryResponseArray == null) {
            log.error("Inventory service did not respond.");
            return new OrderResponseDto(HttpStatus.SERVICE_UNAVAILABLE,
                    "Order could not be placed. Inventory service did not respond.",
                    null);
        }

        boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponseDto::isInStock);

        if (allProductsInStock) {
            Order order = new Order();
            order.setCustomerId(orderRequestDto.getCustomerId());
            order.setOrderNumber(UUID.randomUUID().toString());
            order.setOrderLineItems(orderRequestDto.getOrderLineItemsDtoList()
                    .stream()
                    .map(this::mapDtoLineItemsToLineItems)
                    .toList());
            orderRepository.save(order);
            log.info("New order placed: {}.", order);
            return new OrderResponseDto(HttpStatus.CREATED,
                    "Order placed successfully.",
                    order.getOrderNumber());
        } else {
            log.info("Not all products are in stock.");
            return new OrderResponseDto(HttpStatus.BAD_REQUEST,
                    "Not all products are in stock.",
                    null);
        }
    }

    private OrderLineItems mapDtoLineItemsToLineItems(OrderRequestLineItemsDto dtoLineItem) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(dtoLineItem.getPrice());
        orderLineItems.setQuantity(dtoLineItem.getQuantity());
        orderLineItems.setSkuCode(dtoLineItem.getSkuCode());
        return orderLineItems;
    }
}
