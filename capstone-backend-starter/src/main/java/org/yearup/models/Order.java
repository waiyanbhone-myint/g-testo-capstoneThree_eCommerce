package org.yearup.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderId;
    private int userId;
    private LocalDateTime orderDate;
    private BigDecimal total;
    private List<OrderLineItem> lineItems;

    public Order() {
        this.lineItems = new ArrayList<>();
        this.orderDate = LocalDateTime.now();
        this.total = BigDecimal.ZERO;
    }

    public Order(int userId) {
        this();
        this.userId = userId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<OrderLineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<OrderLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public void addLineItem(OrderLineItem lineItem) {
        this.lineItems.add(lineItem);
    }

    public void calculateTotal() {
        this.total = lineItems.stream()
                .map(OrderLineItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}