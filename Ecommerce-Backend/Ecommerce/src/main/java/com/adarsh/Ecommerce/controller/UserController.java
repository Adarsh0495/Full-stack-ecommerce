package com.adarsh.Ecommerce.controller;

import com.adarsh.Ecommerce.model.Product;
import com.adarsh.Ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController
{
    private final UserService service;

    @GetMapping("/user/products")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Product>> getAllProducts()
    {
        List<Product> products= service.getAllProducts();
       return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/user/product/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Product> getProductByIdForUser(@PathVariable int id)
        {
            Product product= service.getProductByIdForUser(id);
            return new ResponseEntity<>(product,HttpStatus.OK);
        }

    @GetMapping("/user/products/search")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword)
        {
            List<Product> products=service.searchProducts(keyword);
            return new ResponseEntity<>(products,HttpStatus.OK);
        }

    @GetMapping("/user/products/{category}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category)
        {
          List<Product> products= service.getProductsByCategory(category);
          return new ResponseEntity<>(products,HttpStatus.OK);
        }


}
