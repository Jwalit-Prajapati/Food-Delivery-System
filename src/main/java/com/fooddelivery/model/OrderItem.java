package com.fooddelivery.model;

import java.io.Serializable;

import lombok.*;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "food_item_id", nullable = false)
    private Long foodItemId;

    @Column(nullable = false)
    private int quantity;

    /** Unit price of the food item at time of order. */
    @Column(nullable = false)
    private BigDecimal price;

    /**
     * Line-item subtotal = price × quantity.
     * Stored in DB for reporting; always recomputed on save.
     */
    @Column(nullable = false)
    private BigDecimal subtotal;

}
