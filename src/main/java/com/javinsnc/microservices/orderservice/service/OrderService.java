package com.javinsnc.microservices.orderservice.service;

import com.javinsnc.microservices.orderservice.dto.OrderRequest;
import com.javinsnc.microservices.orderservice.model.Order;
import com.javinsnc.microservices.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order(
                orderRequest.id(),
                orderRequest.orderNumber(),
                orderRequest.skuCode(),
                orderRequest.price(),
                orderRequest.quantity()
        );

        orderRepository.save(order);
    }
}
