package com.example.grocart_backend.controller;

import com.example.grocart_backend.model.InternetItem;
import com.example.grocart_backend.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/android/grocery_delivery_app")
public class ItemController {

    @Autowired
    private ItemRepository repository;

    /**
     * Fetches all available grocery items.
     *
     * @return A list of all InternetItem objects.
     */
    @GetMapping("/items.json")
    public List<InternetItem> getAllItems() {
        return repository.findAll();
    }

    /**
     * Adds a new grocery item to the repository.
     *
     * @param item The item details to be added.
     * @return The saved InternetItem object.
     */
    @PostMapping("/add")
    public InternetItem addItem(@RequestBody InternetItem item) {
        return repository.save(item);
    }
}