package com.example.seckill.service.impl;

import com.example.seckill.controller.OrderController;
import com.example.seckill.entity.Order;
import com.example.seckill.entity.OrderItem;
import com.example.seckill.entity.Product;
import com.example.seckill.repository.OrderRepository;
import com.example.seckill.repository.ProductRepository;
import com.example.seckill.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Page<Order> getOrders(int page, int size) {
        return orderRepository.findAll(PageRequest.of(page - 1, size));
    }

    @Override
    public Order createOrder(List<OrderController.OrderItemRequest> orderItemRequests) {
        Order order = new Order();
        List<OrderItem> orderItems = orderItemRequests.stream()
                .map(request -> {
                    Product product = productRepository.findById(request.getProductId())
                            .orElseThrow(() -> new RuntimeException("商品不存在"));
                    OrderItem orderItem = new OrderItem();
                    orderItem.setProduct(product);
                    orderItem.setQuantity(request.getQuantity());
                    return orderItem;
                })
                .collect(Collectors.toList());

        order.setItems(orderItems);
        order.setCreatedAt(LocalDateTime.now());

        // 计算总价
        BigDecimal totalPrice = orderItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPrice(totalPrice);

        return orderRepository.save(order);
    }
}