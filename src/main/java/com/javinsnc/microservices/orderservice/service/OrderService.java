package com.javinsnc.microservices.orderservice.service;

import com.javinsnc.microservices.orderservice.client.InventoryClient;
import com.javinsnc.microservices.orderservice.dto.OrderRequest;
import com.javinsnc.microservices.orderservice.model.Order;
import com.javinsnc.microservices.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    public OrderService(OrderRepository orderRepository, InventoryClient inventoryClient) {
        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
    }

    public void placeOrder(OrderRequest orderRequest) {
        final var isProductInStock = inventoryClient.isInStock(orderRequest.skuCode(), orderRequest.quantity());

        if (isProductInStock) {
            Order order = new Order(
                    orderRequest.id(),
                    orderRequest.orderNumber(),
                    orderRequest.skuCode(),
                    orderRequest.price(),
                    orderRequest.quantity()
            );

            orderRepository.save(order);
        } else {
            throw new RuntimeException("Product is out of stock");
        }
    }
}
