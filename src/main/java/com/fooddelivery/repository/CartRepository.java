package com.fooddelivery.repository;

import com.fooddelivery.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserId(Long userId);

    @Modifying
    @Query("UPDATE Cart c SET c.restaurantId = :restaurantId WHERE c.id = :cartId")
    int updateRestaurant(Long cartId, Long restaurantId);
}
