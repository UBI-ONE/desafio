package com.ubione.order.application;

import com.ubione.order.domain.model.Order;
import com.ubione.order.domain.ports.OrderRepositoryPort;
import com.ubione.order.interfaces.web.dto.OrderResponse;
import com.ubione.order.interfaces.web.mapper.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final OrderMapper mapper;

    public GetOrderUseCase(OrderRepositoryPort orderRepository, OrderMapper mapper) {
        this.orderRepository = orderRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public OrderResponse byId(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
        return mapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderResponse byExternalId(String externalId) {
        Order order = orderRepository.findByExternalId(externalId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + externalId));
        return mapper.toResponse(order);
    }
}
