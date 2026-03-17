package com.example.grocart_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class InternetItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;

    private String itemCategory;

    private String itemQuantity;


    private Integer itemPrice;

    private String imageUrl;
}