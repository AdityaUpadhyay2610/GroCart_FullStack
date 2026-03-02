package com.example.grocart_backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
public class InternetItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("stringResourceId")
    private String itemName;

    @JsonProperty("itemCategoryId")
    private String itemCategory;

    @JsonProperty("itemQuantity")
    private String itemQuantity;

    @JsonProperty("item_price")
    private Integer itemPrice;

    @JsonProperty("imageResourceId")
    private String imageUrl;

    // Default Constructor
    public InternetItem() {}

    // Manual Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getItemCategory() { return itemCategory; }
    public void setItemCategory(String itemCategory) { this.itemCategory = itemCategory; }

    public String getItemQuantity() { return itemQuantity; }
    public void setItemQuantity(String itemQuantity) { this.itemQuantity = itemQuantity; }

    public Integer getItemPrice() { return itemPrice; }
    public void setItemPrice(Integer itemPrice) { this.itemPrice = itemPrice; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}