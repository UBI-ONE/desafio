package com.ubione.order.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders", uniqueConstraints = {
        @UniqueConstraint(name = "uk_orders_external_id", columnNames = "external_id")
})
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false, length = 100)
    private String externalId;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "received_at", nullable = false)
    private OffsetDateTime receivedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItemEntity> items = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public OffsetDateTime getReceivedAt() {
        return receivedAt;
    }

    public List<OrderItemEntity> getItems() {
        return items;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setReceivedAt(OffsetDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    public void setItems(List<OrderItemEntity> items) {
        this.items = items;
    }
}
