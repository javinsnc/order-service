package com.javinsnc.microservices.orderservice.repository;

import com.javinsnc.microservices.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
