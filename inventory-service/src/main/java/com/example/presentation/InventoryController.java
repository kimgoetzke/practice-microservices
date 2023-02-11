package com.example.presentation;

import com.example.business.InventoryRequestDto;
import com.example.business.InventoryResponseDto;
import com.example.business.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class InventoryController {
    private final InventoryService inventoryService;

    @PostMapping("api/inventory")
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponseDto> requestInventoryStatus(@RequestBody List<InventoryRequestDto> requestDtoList) {
        log.info("Valid request to check inventory for list: {}.", requestDtoList);
        List<InventoryResponseDto> responseDtoList = inventoryService.isInStock(requestDtoList);
        inventoryService.updateStock(requestDtoList, responseDtoList);
        return responseDtoList;
    }
}