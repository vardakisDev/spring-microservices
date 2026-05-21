package com.interview.cartservice.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Embeddable
public class PurchaseItemEmbeddable {

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "product_name", nullable = false, length = 150)
    private String productName;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "line_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal lineTotal;

    @Column(name = "purchased_at", nullable = false)
    private OffsetDateTime purchasedAt;

    protected PurchaseItemEmbeddable() {
    }

    public PurchaseItemEmbeddable(UUID productId,
                                  String productName,
                                  BigDecimal unitPrice,
                                  int quantity,
                                  BigDecimal lineTotal,
                                  OffsetDateTime purchasedAt) {
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.lineTotal = lineTotal;
        this.purchasedAt = purchasedAt;
    }

    public UUID getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public OffsetDateTime getPurchasedAt() {
        return purchasedAt;
    }
}