package com.adarsh.Ecommerce.controller;

import com.adarsh.Ecommerce.dto.CartDto;
import com.adarsh.Ecommerce.model.User;
import com.adarsh.Ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class CartController {

    private final CartService service;

    @GetMapping("/cart/get")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CartDto>> getCart() {
        Optional<User> optionalUser = service.getCurrentUser();
        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User user = optionalUser.get();

        List<CartDto> cartItems = service.getCartItems(user);
        return new ResponseEntity<>(cartItems, HttpStatus.OK);
    }

    @PostMapping("/cart/add")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addItemToCart(@RequestParam int productId, @RequestParam int quantity) {
        Optional<User> optionalUser = service.getCurrentUser();
        if (optionalUser.isEmpty()) {
            return new ResponseEntity<>("User not authenticated",HttpStatus.UNAUTHORIZED);
        }
        User user = optionalUser.get();

        try {
            service.addItemToCart(user.getId(), productId, quantity);
            List<CartDto> updatedCart = service.getCartItems(user);
            return new ResponseEntity<>(updatedCart,HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/cart/delete/{cartId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteCartItemById(@PathVariable Long cartId) {
        Optional<User> currentUser = service.getCurrentUser();
        if (currentUser.isEmpty())
        {
            return new ResponseEntity<>("User not found",HttpStatus.UNAUTHORIZED);
        }
        User user=currentUser.get();

        try {
            List<CartDto> updatedCart=service.deleteCartItemById(cartId,user);
            return new ResponseEntity<>(updatedCart,HttpStatus.OK);
        }
        catch (RuntimeException e)
        {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }

    }

    @DeleteMapping("/cart/delete-all")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteAllCartItems()
    {
       Optional<User> currentUser=service.getCurrentUser();
       if (currentUser.isEmpty())
       {
          return new ResponseEntity<>("User not found",HttpStatus.UNAUTHORIZED);
       }
       User user=currentUser.get();

       try {
           List<CartDto> updatedCart=service.deleteAllCartItems(user);
           return new ResponseEntity<>(updatedCart,HttpStatus.OK);
       } catch (RuntimeException e) {
           return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
       }
    }

    @PutMapping("/cart/update/{cartId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CartDto>>updateCartItemQuantity(@PathVariable Long cartId,
                                                               @RequestParam int quantity)
    {
        Optional<User> optionalUser=service.getCurrentUser();
        if (optionalUser.isEmpty())
        {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        User user=optionalUser.get();
        try {
                service.updateCartItemQuantity(cartId,quantity,user);
                List<CartDto> updatedCart=service.getCartItems(user);
                return new ResponseEntity<>(updatedCart,HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(List.of(),HttpStatus.BAD_REQUEST);
        }
    }

}