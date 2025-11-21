package com.ubione.order.infrastructure.persistence.repository;

import com.ubione.order.domain.model.Order;
import com.ubione.order.domain.model.OrderItem;
import com.ubione.order.domain.model.OrderStatus;
import com.ubione.order.domain.ports.OrderRepositoryPort;
import com.ubione.order.infrastructure.persistence.entity.OrderEntity;
import com.ubione.order.infrastructure.persistence.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class OrderRepositoryAdapter implements OrderRepositoryPort {

    private final SpringDataOrderRepository springDataOrderRepository;

    public OrderRepositoryAdapter(SpringDataOrderRepository springDataOrderRepository) {
        this.springDataOrderRepository = springDataOrderRepository;
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = toEntity(order);
        OrderEntity saved = springDataOrderRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return springDataOrderRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Order> findByExternalId(String externalId) {
        return springDataOrderRepository.findByExternalId(externalId).map(this::toDomain);
    }

    @Override
    public boolean existsByExternalId(String externalId) {
        return springDataOrderRepository.existsByExternalId(externalId);
    }

    private OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setId(order.getId());
        entity.setExternalId(order.getExternalId());
        entity.setStatus(order.getStatus().name());
        entity.setTotalAmount(order.getTotalAmount());
        entity.setReceivedAt(order.getReceivedAt());

        List<OrderItemEntity> items = order.getItems().stream().map(i -> {
            OrderItemEntity e = new OrderItemEntity();
            e.setId(i.getId());
            e.setProductCode(i.getProductCode());
            e.setDescription(i.getDescription());
            e.setQuantity(i.getQuantity());
            e.setUnitPrice(i.getUnitPrice());
            e.setLineTotal(i.getLineTotal());
            e.setOrder(entity);
            return e;
        }).collect(Collectors.toList());
        entity.setItems(items);
        return entity;
    }

    private Order toDomain(OrderEntity entity) {
        Order order = new Order();
        order.setId(entity.getId());
        order.setExternalId(entity.getExternalId());
        order.setStatus(OrderStatus.valueOf(entity.getStatus()));
        order.setTotalAmount(entity.getTotalAmount());
        order.setReceivedAt(entity.getReceivedAt());
        List<OrderItem> items = entity.getItems().stream().map(e -> {
            OrderItem i = new OrderItem();
            i.setId(e.getId());
            i.setProductCode(e.getProductCode());
            i.setDescription(e.getDescription());
            i.setQuantity(e.getQuantity());
            i.setUnitPrice(e.getUnitPrice());
            i.setLineTotal(e.getLineTotal());
            return i;
        }).collect(Collectors.toList());
        order.setItems(items);
        return order;
    }
}
