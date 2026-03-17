package com.example.grocart_backend.repository;

import com.example.grocart_backend.model.InternetItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<InternetItem, Long> {
    /**
     * Finds items by category, ignoring case.
     * @param category The category name.
     * @return List of InternetItem objects matching the category.
     */
    List<InternetItem> findByItemCategoryIgnoreCase(String category);
}