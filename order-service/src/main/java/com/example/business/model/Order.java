package com.example.business.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "orders")
@ToString
@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderNumber;
    private Long customerId;
    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderLineItem> orderLineItems;
}
