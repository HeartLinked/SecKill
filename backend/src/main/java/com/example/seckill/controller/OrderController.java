package com.example.seckill.controller;

import com.example.seckill.entity.Order;
import com.example.seckill.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public Response getOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        Page<Order> orderPage = orderService.getOrders(page, size);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("content", orderPage.getContent());
        responseMap.put("totalPages", orderPage.getTotalPages());
        responseMap.put("totalElements", orderPage.getTotalElements());
        responseMap.put("size", orderPage.getSize());
        responseMap.put("number", orderPage.getNumber());

        return new Response(200, "获取订单列表成功", responseMap);
    }

    @PostMapping
    public Response createOrder(@RequestBody List<OrderItemRequest> orderItemRequests) {
        Order order = orderService.createOrder(orderItemRequests);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("order", order);

        return new Response(200, "创建订单成功", responseMap);
    }

    // 请求体类
    public static class OrderItemRequest {
        private Long productId;
        private int quantity;

        // Getter and Setter
        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}