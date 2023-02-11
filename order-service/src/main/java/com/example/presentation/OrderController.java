package com.example.presentation;

import com.example.business.Order;
import com.example.business.OrderRequestDto;
import com.example.business.OrderResponseDto;
import com.example.business.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;

    private HttpHeaders getDefaultHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");
        return responseHeaders;
    }

    @PostMapping("api/order")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<OrderResponseDto> placeOrder(@RequestBody OrderRequestDto orderRequestDto) throws JsonProcessingException {
        log.info("Valid order request received: " + orderRequestDto);
        OrderResponseDto orderResponseDto = orderService.placeOrder(orderRequestDto);
        return new ResponseEntity<>(orderResponseDto, getDefaultHeaders(), orderResponseDto.getStatus());
    }
}
