package com.adarsh.Ecommerce.service;

import com.adarsh.Ecommerce.dto.CartDto;
import com.adarsh.Ecommerce.model.Cart;
import com.adarsh.Ecommerce.model.Product;
import com.adarsh.Ecommerce.model.User;
import com.adarsh.Ecommerce.repository.CartRepository;
import com.adarsh.Ecommerce.repository.ProductRepository;
import com.adarsh.Ecommerce.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService
{
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final EntityManager entityManager;

    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findUserByUsername(username);
    }

    public List<CartDto> getCartItems(User user) {
        entityManager.clear();
        List<Cart> cartItems = cartRepository.findByUser(user);
        return cartItems.stream()
                .map(cart -> {
                    assert cart.getProduct() != null;
                    return new CartDto(
                            cart.getId()!= null ? cart.getId().intValue() : -1,
                            cart.getProduct().getProductId(),
                            cart.getProduct().getProductName(),
                            cart.getProduct().getProductPrice(),
                            cart.getQuantity(),
                            cart.getProduct().getCategory(),
                            cart.getProduct().getImage()
                    );
                })
                .toList();
    }

    public void addItemToCart(int userId, int productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        Optional<Cart> existingCart = cartRepository.findByUserAndProduct(user, product);
        Cart cart;
        if (existingCart.isPresent()) {
            cart = existingCart.get();
            cart.setQuantity(cart.getQuantity() + quantity);
        } else {
            cart = new Cart(user, product, quantity);
        }

         cartRepository.save(cart);
    }

    public List<CartDto> deleteCartItemById(Long cartId, User user) {
        Optional<Cart> optionalCart = cartRepository.findByIdAndUser(cartId, user);
        if (optionalCart.isEmpty()) {
            throw new RuntimeException("Cart item not found or does not belong to the user: " + cartId);
        }
        Cart cart = optionalCart.get();
        cartRepository.delete(cart);
        return getCartItems(user);
    }

    @Transactional
    public List<CartDto> deleteAllCartItems(User user)
    {
        cartRepository.deleteByUser(user);
        return getCartItems(user);
    }

    @Transactional
    public void updateCartItemQuantity(Long cartId, int quantity, User user) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        Optional<Cart> optionalCart = cartRepository.findByIdAndUser(cartId, user);
        if (optionalCart.isEmpty()) {
            throw new RuntimeException("Cart item not found or does not belong to user: " + cartId);
        }
        Cart cart = optionalCart.get();
        cart.setQuantity(quantity);
        cartRepository.saveAndFlush(cart);
    }
}