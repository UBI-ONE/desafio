package com.ubione.order.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {

    private Long id;
    private String externalId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private OffsetDateTime receivedAt;
    private List<OrderItem> items = new ArrayList<>();

    public Order() {
        this.status = OrderStatus.RECEIVED;
        this.totalAmount = BigDecimal.ZERO;
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
        recalculateTotal();
    }

    public void recalculateTotal() {
        this.totalAmount = items.stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // getters and setters

    public Long getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public OffsetDateTime getReceivedAt() {
        return receivedAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setReceivedAt(OffsetDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
        recalculateTotal();
    }
}
