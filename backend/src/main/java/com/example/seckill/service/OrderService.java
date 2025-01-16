package com.example.seckill.service;

import com.example.seckill.controller.OrderController;
import com.example.seckill.entity.Order;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {
    Page<Order> getOrders(int page, int size);
    Page<Order> getOrders(int page, int size, Integer userId);
    Order createOrder(List<OrderController.OrderItemRequest> orderItemRequests, Integer userId);
}