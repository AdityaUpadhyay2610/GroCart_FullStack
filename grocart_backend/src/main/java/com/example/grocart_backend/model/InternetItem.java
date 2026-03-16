package com.example.grocart_backend.model;

import jakarta.persistence.*;
import lombok.Data; // If using Lombok, otherwise generate getters/setters

@Entity
@Data
public class InternetItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;

    private String itemCategory;

    private String itemQuantity;


    private Integer itemPrice; // Using Integer instead of int is safer for JSON parsing

    private String imageUrl;
}