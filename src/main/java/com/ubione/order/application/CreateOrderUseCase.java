package com.ubione.order.application;

import com.ubione.order.domain.model.Order;
import com.ubione.order.domain.model.OrderItem;
import com.ubione.order.domain.model.OrderStatus;
import com.ubione.order.domain.ports.OrderEventPublisherPort;
import com.ubione.order.domain.ports.OrderRepositoryPort;
import com.ubione.order.domain.ports.SendOrderToProductBPort;
import com.ubione.order.interfaces.web.dto.CreateOrderRequest;
import com.ubione.order.interfaces.web.dto.CreateOrderRequest.OrderItemRequest;
import com.ubione.order.interfaces.web.dto.OrderResponse;
import com.ubione.order.interfaces.web.mapper.OrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@Service
public class CreateOrderUseCase {

    private final OrderRepositoryPort orderRepository;
    private final SendOrderToProductBPort sendOrderToProductBPort;
    private final OrderEventPublisherPort eventPublisher;
    private final OrderMapper mapper;

    public CreateOrderUseCase(OrderRepositoryPort orderRepository,
                              SendOrderToProductBPort sendOrderToProductBPort,
                              OrderEventPublisherPort eventPublisher,
                              OrderMapper mapper) {
        this.orderRepository = orderRepository;
        this.sendOrderToProductBPort = sendOrderToProductBPort;
        this.eventPublisher = eventPublisher;
        this.mapper = mapper;
    }

    @Transactional
    public OrderResponse execute(CreateOrderRequest request) {
        if (orderRepository.existsByExternalId(request.getExternalId())) {
            Order existing = orderRepository.findByExternalId(request.getExternalId()).get();
            return mapper.toResponse(existing);
        }

        Order order = new Order();
        order.setExternalId(request.getExternalId());
        order.setReceivedAt(OffsetDateTime.now());
        order.setStatus(OrderStatus.RECEIVED);

        order.setItems(request.getItems().stream().map(this::toDomainItem).collect(Collectors.toList()));
        order.recalculateTotal();
        order.setStatus(OrderStatus.PROCESSED);

        Order saved = orderRepository.save(order);

        eventPublisher.publishOrderCreated(saved);

        try {
            sendOrderToProductBPort.send(saved);
            saved.setStatus(OrderStatus.SENT_TO_PRODUCT_B);
        } catch (Exception e) {
            saved.setStatus(OrderStatus.ERROR_SENDING_TO_PRODUCT_B);
        }

        saved = orderRepository.save(saved);

        return mapper.toResponse(saved);
    }

    private OrderItem toDomainItem(OrderItemRequest dto) {
        OrderItem item = new OrderItem();
        item.setProductCode(dto.getProductCode());
        item.setDescription(dto.getDescription());
        item.setQuantity(dto.getQuantity());
        item.setUnitPrice(dto.getUnitPrice());
        item.calculateLineTotal();
        return item;
    }
}
