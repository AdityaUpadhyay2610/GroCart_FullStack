package com.example.grocart_backend.controller;

import com.example.grocart_backend.model.CartItem;
import com.example.grocart_backend.repository.CartRepository;
import com.example.grocart_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves all cart items for a specific user.
     *
     * @param userId The ID of the user whose cart items are to be fetched.
     * @return ResponseEntity containing the list of cart items.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItem>> getCart(@PathVariable Long userId) {
        List<CartItem> items = cartRepository.findByUserId(userId);
        return ResponseEntity.ok(items);
    }

    /**
     * Adds a new item to the user's cart or updates the quantity if it already exists.
     *
     * @param userId The ID of the user.
     * @param newItem The item to be added to the cart.
     * @return ResponseEntity with success or error message.
     */
    @PostMapping("/add/{userId}")
    public synchronized ResponseEntity<?> addToCart(@PathVariable Long userId, @RequestBody CartItem newItem) {
        return userRepository.findById(userId).map(user -> {


            if (newItem.getItemName() == null || newItem.getItemName().isEmpty()) {
                return ResponseEntity.badRequest().body("Item name missing in request");
            }

            Optional<CartItem> existingItem = cartRepository.findByUserIdAndItemName(userId, newItem.getItemName());

            if (existingItem.isPresent()) {

                CartItem itemToUpdate = existingItem.get();
                int currentQty = itemToUpdate.getQuantity() != null ? itemToUpdate.getQuantity() : 0;

                int newQty = newItem.getQuantity() != null ? newItem.getQuantity() : 1;

                itemToUpdate.setQuantity(currentQty + newQty);
                cartRepository.save(itemToUpdate);
                return ResponseEntity.ok("Quantity updated for " + newItem.getItemName());
            } else {

                newItem.setUser(user);

                if (newItem.getQuantity() == null) newItem.setQuantity(1);
                if (newItem.getItemPrice() == null) newItem.setItemPrice(0);

                cartRepository.save(newItem);
                return ResponseEntity.ok("New item added to MySQL: " + newItem.getItemName());
            }
        }).orElse(ResponseEntity.badRequest().body("User not found"));
    }

    /**
     * Clears all items from a user's cart.
     *
     * @param userId The ID of the user whose cart should be cleared.
     * @return ResponseEntity indicating success.
     */
    @Transactional
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<?> clearCart(@PathVariable Long userId) {
        cartRepository.deleteByUserId(userId);
        return ResponseEntity.ok("Cart cleared for user ID: " + userId);
    }

    /**
     * Decreases the quantity of an item in the user's cart.
     * Deletes the item if quantity drops to 0.
     *
     * @param userId The ID of the user.
     * @param itemRequest The item whose quantity is to be decreased.
     * @return ResponseEntity with success or error message.
     */
    @PostMapping("/decrease/{userId}")
    public synchronized ResponseEntity<?> decreaseCartItem(@PathVariable Long userId, @RequestBody CartItem itemRequest) {
        if (itemRequest.getItemName() == null || itemRequest.getItemName().isEmpty()) {
            return ResponseEntity.badRequest().body("Item name missing in request");
        }
        Optional<CartItem> existingItem = cartRepository.findByUserIdAndItemName(userId, itemRequest.getItemName());
        
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int currentQty = item.getQuantity() != null ? item.getQuantity() : 0;
            
            if (currentQty > 1) {
                item.setQuantity(currentQty - 1);
                cartRepository.save(item);
                return ResponseEntity.ok("Quantity decreased");
            } else {
                cartRepository.delete(item);
                return ResponseEntity.ok("Item removed from cart");
            }
        }
        return ResponseEntity.badRequest().body("Item not found in cart");
    }
}