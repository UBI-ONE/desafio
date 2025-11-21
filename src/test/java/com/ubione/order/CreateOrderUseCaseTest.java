package com.ubione.order.application;

import com.ubione.order.domain.model.Order;
import com.ubione.order.domain.model.OrderStatus;
import com.ubione.order.domain.ports.OrderEventPublisherPort;
import com.ubione.order.domain.ports.OrderRepositoryPort;
import com.ubione.order.domain.ports.SendOrderToProductBPort;
import com.ubione.order.interfaces.web.dto.CreateOrderRequest;
import com.ubione.order.interfaces.web.dto.CreateOrderRequest.OrderItemRequest;
import com.ubione.order.interfaces.web.dto.OrderResponse;
import com.ubione.order.interfaces.web.mapper.OrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CreateOrderUseCaseTest {

    private OrderRepositoryPort orderRepository;
    private SendOrderToProductBPort sendOrderToProductBPort;
    private OrderEventPublisherPort eventPublisher;
    private OrderMapper mapper;
    private CreateOrderUseCase useCase;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepositoryPort.class);
        sendOrderToProductBPort = mock(SendOrderToProductBPort.class);
        eventPublisher = mock(OrderEventPublisherPort.class);
        mapper = new OrderMapper();
        useCase = new CreateOrderUseCase(orderRepository, sendOrderToProductBPort, eventPublisher, mapper);
    }

    @Test
    void shouldCreateNewOrderWhenExternalIdDoesNotExist() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setExternalId("ORDER-1");
        OrderItemRequest item = new OrderItemRequest();
        item.setProductCode("P1");
        item.setDescription("Beer");
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("10.00"));
        request.setItems(Collections.singletonList(item));

        when(orderRepository.existsByExternalId("ORDER-1")).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = useCase.execute(request);

        assertNotNull(response);
        assertEquals("ORDER-1", response.getExternalId());
        assertEquals(new BigDecimal("20.00"), response.getTotalAmount());
        assertTrue(response.getStatus() == OrderStatus.SENT_TO_PRODUCT_B
                || response.getStatus() == OrderStatus.ERROR_SENDING_TO_PRODUCT_B);

        verify(eventPublisher, times(1)).publishOrderCreated(any(Order.class));
        verify(sendOrderToProductBPort, times(1)).send(any(Order.class));
        verify(orderRepository, atLeast(1)).save(any(Order.class));
    }

    @Test
    void shouldReturnExistingOrderWhenExternalIdAlreadyExists() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setExternalId("ORDER-2");

        Order existing = new Order();
        existing.setExternalId("ORDER-2");
        existing.setStatus(OrderStatus.PROCESSED);

        when(orderRepository.existsByExternalId("ORDER-2")).thenReturn(true);
        when(orderRepository.findByExternalId("ORDER-2")).thenReturn(Optional.of(existing));

        OrderResponse response = useCase.execute(request);

        assertEquals("ORDER-2", response.getExternalId());
        verify(orderRepository, never()).save(any());
        verify(sendOrderToProductBPort, never()).send(any());
        verify(eventPublisher, never()).publishOrderCreated(any());
    }
}
