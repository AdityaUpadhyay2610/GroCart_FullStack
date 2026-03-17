package com.example.grocart_backend.repository;


import com.example.grocart_backend.model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    /**
     * Finds all orders placed by a specific user.
     * @param userId The ID of the user.
     * @return List of OrderEntity objects.
     */
    List<OrderEntity> findByUserId(Long userId);
}
