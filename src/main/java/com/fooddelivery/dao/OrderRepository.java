package com.fooddelivery.dao;

import com.fooddelivery.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);

    List<Order> findByRestaurantIdOrderByOrderDateDesc(Long restaurantId);

    List<Order> findByRestaurantIdAndStatusOrderByOrderDateDesc(Long restaurantId, Order.Status status);

    List<Order> findByDeliveryPartnerIdAndStatusOrderByOrderDateDesc(Long deliveryPartnerId, Order.Status status);

    @Modifying
    @Query("UPDATE Order o SET o.status = :status WHERE o.id = :id")
    int updateStatus(Long id, Order.Status status);

    @Modifying
    @Query("UPDATE Order o SET o.paymentStatus = :status WHERE o.id = :id")
    int updatePaymentStatus(Long id, Order.PaymentStatus status);

    @Modifying
    @Query("UPDATE Order o SET o.deliveryPartnerId = :partnerId WHERE o.id = :id")
    int assignDeliveryPartner(Long id, Long partnerId);
    List<Order> findByStatusOrderByOrderDateDesc(Order.Status status);

    List<Order> findByDeliveryPartnerIdOrderByOrderDateDesc(Long deliveryPartnerId);

    int countByStatus(Order.Status status);

    @Query("SELECT COUNT(o) FROM Order o WHERE DATE(o.orderDate) = CURRENT_DATE")
    long countToday();

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o")
    java.math.BigDecimal revenueAllTime();

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE DATE(o.orderDate) = CURRENT_DATE")
    java.math.BigDecimal revenueToday();
}
