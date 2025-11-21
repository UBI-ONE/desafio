package com.ubione.order.interfaces.web.mapper;

import com.ubione.order.domain.model.Order;
import com.ubione.order.domain.model.OrderItem;
import com.ubione.order.interfaces.web.dto.OrderResponse;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        OrderResponse dto = new OrderResponse();
        dto.setId(order.getId());
        dto.setExternalId(order.getExternalId());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setReceivedAt(order.getReceivedAt());
        dto.setItems(order.getItems().stream().map(this::toItemResponse).collect(Collectors.toList()));
        return dto;
    }

    private OrderResponse.OrderItemResponse toItemResponse(OrderItem item) {
        OrderResponse.OrderItemResponse dto = new OrderResponse.OrderItemResponse();
        dto.setId(item.getId());
        dto.setProductCode(item.getProductCode());
        dto.setDescription(item.getDescription());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setLineTotal(item.getLineTotal());
        return dto;
    }
}
