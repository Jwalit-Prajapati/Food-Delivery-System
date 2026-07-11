package com.fooddelivery.repository;

import com.fooddelivery.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCartId(Long cartId);

    Optional<CartItem> findByCartIdAndFoodItemId(Long cartId, Long foodItemId);

    @Modifying
    @Query("UPDATE CartItem c SET c.quantity = :quantity WHERE c.id = :itemId")
    int updateItemQuantity(Long itemId, int quantity);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.cartId = :cartId")
    int deleteByCartId(Long cartId);
}
