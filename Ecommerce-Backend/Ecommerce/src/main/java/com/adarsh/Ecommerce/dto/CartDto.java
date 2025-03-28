package com.adarsh.Ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDto {
    private int cartId;
    private int productId;
    private String productName;
    private BigDecimal productPrice;
    private String category;
    private int quantity;
    private String image;

    public CartDto(int cartId, int productId, String productName, BigDecimal productPrice, int quantity,String category,String image) {
        this.cartId = cartId;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.category=category;
        this.image=image;
    }
}