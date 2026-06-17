package com.fooddelivery.model;

import java.math.BigDecimal;

/**
 * Simple DTO holding computed pricing values for a cart/order.
 * Placed under the model package as requested.
 */
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pricing {

    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal deliveryFee;
    private BigDecimal total;

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Pricing{" +
                "subtotal=" + subtotal +
                ", tax=" + tax +
                ", deliveryFee=" + deliveryFee +
                ", total=" + total +
                '}';
    }
}

