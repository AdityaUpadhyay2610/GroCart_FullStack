package com.example.grocart_backend.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Integer totalAmount;
    @Column(columnDefinition = "TEXT")
    private String itemDetails;

    private Timestamp orderDate = new Timestamp(System.currentTimeMillis());

    public OrderEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Integer totalAmount) { this.totalAmount = totalAmount; }

    public Timestamp getOrderDate() { return orderDate; }
    public void setOrderDate(Timestamp orderDate) { this.orderDate = orderDate; }

    public String getItemDetails() { return itemDetails; }
    public void setItemDetails(String itemDetails) { this.itemDetails = itemDetails; }


}