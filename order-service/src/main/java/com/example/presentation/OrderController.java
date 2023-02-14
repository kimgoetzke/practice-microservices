package com.example.presentation;

import com.example.business.dtos.OrderRequestDto;
import com.example.business.dtos.OrderResponseDto;
import com.example.business.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    @TimeLimiter(name = "inventory")
    @Retry(name = "inventory")
    public CompletableFuture<ResponseEntity<OrderResponseDto>> placeOrder(@Valid @RequestBody OrderRequestDto orderRequestDto) throws JsonProcessingException {
        log.info("Valid order request received: " + orderRequestDto);
        OrderResponseDto orderResponseDto = orderService.placeOrder(orderRequestDto);
        return CompletableFuture.supplyAsync(() -> new ResponseEntity<>(orderResponseDto, getDefaultHeaders(), orderResponseDto.getStatus()));
    }

    public CompletableFuture<ResponseEntity<Map<String, String>>> fallbackMethod(@Valid @RequestBody OrderRequestDto orderRequestDto, RuntimeException exception) {
        log.error("Inventory service is unresponsive. Details: " + exception);
        return CompletableFuture.supplyAsync(() -> new ResponseEntity<>(Collections.singletonMap("message", "Something went wrong. Circuit breaker triggered. Try again later."), getDefaultHeaders(), HttpStatus.GATEWAY_TIMEOUT));
    }

}
