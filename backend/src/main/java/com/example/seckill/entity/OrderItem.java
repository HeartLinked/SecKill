package com.example.seckill.entity;
import lombok.Data;

import jakarta.persistence.*;

@Entity
@Table(name = "t_order_item")
@Data
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;
}

