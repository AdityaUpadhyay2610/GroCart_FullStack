package com.example.grocart_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "item_price")
    private Integer itemPrice;

    @Column(name = "image_url")
    private String imageUrl;

    private Integer quantity;

    public CartItem() {}

    public CartItem(Long id, User user, String itemName, Integer itemPrice, String imageUrl, Integer quantity) {
        this.id = id;
        this.user = user;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public Integer getItemPrice() { return itemPrice; }
    public void setItemPrice(Integer itemPrice) { this.itemPrice = itemPrice; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}