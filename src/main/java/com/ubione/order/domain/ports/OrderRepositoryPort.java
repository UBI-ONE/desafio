package com.ubione.order.domain.ports;

import com.ubione.order.domain.model.Order;

import java.util.Optional;

public interface OrderRepositoryPort {

    Order save(Order order);

    Optional<Order> findById(Long id);

    Optional<Order> findByExternalId(String externalId);

    boolean existsByExternalId(String externalId);
}
