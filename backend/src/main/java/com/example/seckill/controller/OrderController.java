package com.example.seckill.controller;

import com.example.seckill.config.RabbitConfig;
import com.example.seckill.entity.Order;
import com.example.seckill.entity.OrderItem;
import com.example.seckill.entity.Product;
import com.example.seckill.filter.JwtFilter;
import com.example.seckill.repository.OrderRepository;
import com.example.seckill.repository.ProductRepository;
import com.example.seckill.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.flogger.Flogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    public OrderController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping
    public Response getOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size,
            HttpServletRequest request
    ) {

        Integer userId = (Integer) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");
        logger.info("get userId = {}", userId);
        logger.info("get username = {}", username);

        Page<Order> orderPage = orderService.getOrders(page, size, userId);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("content", orderPage.getContent());
        responseMap.put("totalPages", orderPage.getTotalPages());
        responseMap.put("totalElements", orderPage.getTotalElements());
        responseMap.put("size", orderPage.getSize());
        responseMap.put("number", orderPage.getNumber());

        return new Response(200, "获取订单列表成功", responseMap);
    }
    private final RabbitTemplate rabbitTemplate;

    @PostMapping
    public Response createOrder(@RequestBody List<OrderItemRequest> orderItemRequests,
                                HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");

        Order order = createOrder(orderItemRequests, userId);

        rabbitTemplate.convertAndSend(
                RabbitConfig.ORDER_EXCHANGE,
                RabbitConfig.ORDER_ROUTING_KEY,
                order
        );

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("order", order);

        return new Response(200, "创建订单成功", responseMap);
    }

    public Order createOrder(List<OrderController.OrderItemRequest> orderItemRequests, Integer userId) {
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
        order.setUserId(userId.longValue());

        // 计算总价
        BigDecimal totalPrice = orderItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPrice(totalPrice);

        return order;
//        return orderRepository.save(order);
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