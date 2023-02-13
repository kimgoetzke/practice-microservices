package com.example.business;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_line_items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderLineItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer quantity;

}
