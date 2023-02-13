package com.example.business;

import com.example.persistence.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponseDto> isInStock(List<InventoryRequestDto> requestDtoList) {
        List<InventoryResponseDto> responseDtoList = new ArrayList<>();

        for (InventoryRequestDto requestDto : requestDtoList) {
            InventoryResponseDto responseDto = new InventoryResponseDto();
            responseDto.setName(requestDto.getName());
            if (inventoryRepository.findByName(requestDto.getName()).isPresent()) {
                responseDto.setInStock(inventoryRepository
                        .findByName(requestDto.getName()).get().getQuantity() > requestDto.getQuantity());
            } else {
                responseDto.setInStock(false);
            }
            responseDtoList.add(responseDto);
        }

        log.info("Response list created: " + responseDtoList);
        return responseDtoList;
    }

    @Transactional
    public void updateStock(List<InventoryRequestDto> requestDtoList, List<InventoryResponseDto> responseDtoList) {
        boolean allProductsInStock = responseDtoList.stream().allMatch(InventoryResponseDto::isInStock);

        if (allProductsInStock) {
            log.info("All items in stock. Inventory will be updated.");
            for (InventoryRequestDto responseDto : requestDtoList) {
                Optional<Inventory> inventory = inventoryRepository.findByName(responseDto.getName());
                if (inventory.isPresent()) {
                    inventory.get().setQuantity(inventory.get().getQuantity() - responseDto.getQuantity());
                    inventoryRepository.save(inventory.get());
                }
            }
        } else {
            log.info("Not all items in stock. No changes processed.");
        }
    }
}