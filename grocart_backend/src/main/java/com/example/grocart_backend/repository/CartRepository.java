package com.example.grocart_backend.repository;

import com.example.grocart_backend.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartItem, Long> {


    /**
     * Finds all cart items for a specific user.
     * @param userId The ID of the user.
     * @return List of CartItem objects.
     */
    List<CartItem> findByUserId(Long userId);

    /**
     * Finds a specific cart item for a user by its name.
     * @param userId The ID of the user.
     * @param itemName The name of the item.
     * @return Optional containing the CartItem if found.
     */
    Optional<CartItem> findByUserIdAndItemName(Long userId, String itemName);

    /**
     * Deletes all cart items for a specific user.
     * @param userId The ID of the user.
     */
    void deleteByUserId(Long userId);
}