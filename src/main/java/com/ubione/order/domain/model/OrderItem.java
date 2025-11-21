package com.ubione.order.domain.model;

import java.math.BigDecimal;

public class OrderItem {

    private Long id;
    private String productCode;
    private String description;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;

    public OrderItem() {
    }

    public void calculateLineTotal() {
        if (unitPrice == null || quantity == null) {
            this.lineTotal = BigDecimal.ZERO;
        } else {
            this.lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    // getters and setters

    public Long getId() {
        return id;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getDescription() {
        return description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }
}
