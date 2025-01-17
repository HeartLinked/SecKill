package com.example.seckill.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // 交换机名称
    public static final String ORDER_EXCHANGE = "order.exchange";
    // 队列名称
    public static final String ORDER_QUEUE = "order.queue";
    // Routing Key
    public static final String ORDER_ROUTING_KEY = "order.routingKey";

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    /**
     * 声明一个 DirectExchange
     */
    @Bean
    public DirectExchange orderExchange() {
        // durable 表示重启后交换机是否依旧存在
        return new DirectExchange(ORDER_EXCHANGE, true, false);
    }

    /**
     * 声明一个 Queue
     */
    @Bean
    public Queue orderQueue() {
        // durable 表示重启后队列是否依旧存在
        return new Queue(ORDER_QUEUE, true);
    }

    /**
     * 将 Queue 和 DirectExchange 进行绑定
     */
    @Bean
    public Binding orderBinding() {
        return BindingBuilder
                .bind(orderQueue())
                .to(orderExchange())
                .with(ORDER_ROUTING_KEY);
    }
}
