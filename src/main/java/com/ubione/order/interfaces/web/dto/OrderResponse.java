package com.ubione.order.interfaces.web.dto;

import com.ubione.order.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public class OrderResponse {

    private Long id;
    private String externalId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private OffsetDateTime receivedAt;
    private List<OrderItemResponse> items;

    public static class OrderItemResponse {
        private Long id;
        private String productCode;
        private String description;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal lineTotal;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getProductCode() {
            return productCode;
        }

        public void setProductCode(String productCode) {
            this.productCode = productCode;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(BigDecimal unitPrice) {
            this.unitPrice = unitPrice;
        }

        public BigDecimal getLineTotal() {
            return lineTotal;
        }

        public void setLineTotal(BigDecimal lineTotal) {
            this.lineTotal = lineTotal;
        }
    }

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

    public List<OrderItemResponse> getItems() {
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

    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
    }
}
