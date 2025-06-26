package org.yearup.models;

import java.math.BigDecimal;

public class OrderLineItem {
    private int orderId;
    private int productId;
    private Product product;
    private int quantity;
    private BigDecimal price;
    private BigDecimal discountPercent;

    public OrderLineItem() {
        this.discountPercent = BigDecimal.ZERO;
    }

    public OrderLineItem(int orderId, int productId, int quantity, BigDecimal price) {
        this();
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        if (product != null) {
            this.productId = product.getProductId();
            this.price = product.getPrice();
        }
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }

    // Calculate line total
    public BigDecimal getLineTotal() {
        BigDecimal basePrice = this.price;
        BigDecimal quantity = new BigDecimal(this.quantity);
        BigDecimal subTotal = basePrice.multiply(quantity);
        BigDecimal discountAmount = subTotal.multiply(discountPercent);
        return subTotal.subtract(discountAmount);
    }
}