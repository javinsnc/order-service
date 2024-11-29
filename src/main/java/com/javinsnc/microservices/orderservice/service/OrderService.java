package com.javinsnc.microservices.orderservice.service;

import com.javinsnc.microservices.orderservice.client.InventoryClient;
import com.javinsnc.microservices.orderservice.dto.OrderRequest;
import com.javinsnc.microservices.orderservice.event.OrderPlacedEvent;
import com.javinsnc.microservices.orderservice.model.Order;
import com.javinsnc.microservices.orderservice.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public OrderService(OrderRepository orderRepository, InventoryClient inventoryClient, KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
        this.kafkaTemplate = kafkaTemplate;
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

            // Send the messages to Kafka Topic
            OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent(
                    orderRequest.orderNumber(),
                    orderRequest.userDetails().email(),
                    orderRequest.userDetails().firstName(),
                    orderRequest.userDetails().lastName()
            );

            log.info("Start - Sending OrderPlacedEvent {} to Kafka topic order-service", orderPlacedEvent);
            kafkaTemplate.send("order-placed", orderPlacedEvent);
            log.info("End - Sending OrderPlacedEvent {} to Kafka topic order-service", orderPlacedEvent);
        } else {
            throw new RuntimeException("Product is out of stock");
        }
    }
}
