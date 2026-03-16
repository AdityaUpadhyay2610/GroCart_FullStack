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

    @GetMapping("/items.json")
    public List<InternetItem> getAllItems() {
        return repository.findAll();
    }

    @PostMapping("/add")
    public InternetItem addItem(@RequestBody InternetItem item) {
        return repository.save(item);
    }
}