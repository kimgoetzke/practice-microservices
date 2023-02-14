package com.example.business;

import com.example.business.dtos.*;
import com.example.business.model.Order;
import com.example.business.model.OrderLineItem;
import com.example.persistence.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
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
    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    public OrderResponseDto placeOrder(OrderRequestDto orderRequestDto) throws JsonProcessingException {
        List<InventoryRequestDto> requestList = new ArrayList<>();
        for (OrderLineItemDto lineItem : orderRequestDto.getOrderLineItemDtoList()) {
            InventoryRequestDto inventoryRequestDto = InventoryRequestDto.builder()
                    .name(lineItem.getName())
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
            order.setOrderLineItems(orderRequestDto.getOrderLineItemDtoList()
                    .stream()
                    .map(this::mapDtoLineItemsToLineItems)
                    .toList());
            orderRepository.save(order);
            triggerKafkaEvent(order);
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

    private void triggerKafkaEvent(Order order) {
        kafkaTemplate.send("orderNotification", OrderEvent.builder()
                .orderNumber(order.getOrderNumber())
                .orderLineItems(order.getOrderLineItems()
                        .stream()
                        .map(this::mapLineItemsToDtoLineItems)
                        .toList()).build());
    }

    private OrderLineItem mapDtoLineItemsToLineItems(OrderLineItemDto dtoLineItem) {
        return OrderLineItem.builder()
                .name(dtoLineItem.getName())
                .price(dtoLineItem.getPrice())
                .quantity(dtoLineItem.getQuantity())
                .build();
    }

    private OrderLineItemDto mapLineItemsToDtoLineItems(OrderLineItem lineItem) {
        return OrderLineItemDto.builder()
                .name(lineItem.getName())
                .price(lineItem.getPrice())
                .quantity(lineItem.getQuantity())
                .build();
    }
}
