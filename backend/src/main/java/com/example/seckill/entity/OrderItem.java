package com.example.seckill.entity;
import lombok.Data;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "t_order_item")
@Data
public class OrderItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;
}

