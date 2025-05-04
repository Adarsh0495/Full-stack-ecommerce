package com.adarsh.Ecommerce.controller;

import com.adarsh.Ecommerce.model.Product;
import com.adarsh.Ecommerce.model.User;
import com.adarsh.Ecommerce.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class ProductController
{

    private final ProductService service;

    @GetMapping("/admin/products")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<Product>> getAllProducts()
    {
        List<Product> products = service.getAllProducts();
        System.out.println("Fetched Products: " + products);
        return new ResponseEntity<>(products,HttpStatus.OK);
    }

    @GetMapping("/admin/product/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> getProductById(@PathVariable int id)
    {
        Product product = service.getProductById(id);
        return new ResponseEntity<>(product,HttpStatus.OK);
    }

    @PostMapping(value = "admin/product/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addProduct(
            @RequestPart("productJson") String productJson,
            @RequestPart("imageFile") MultipartFile imageFile) throws IOException
    {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Product prod = mapper.readValue(productJson, Product.class);

            if (imageFile != null &&!imageFile.isEmpty()) {
               String imageUrl=service.uploadImage(imageFile);
                System.out.println("Cloudinary URL: " + imageUrl);
               prod.setImage(imageUrl);
            }
            Product savedProduct = service.addProduct(prod);
            System.out.println("Saved Product: " + savedProduct);
            return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to add product: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/admin/product/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProductById(
            @PathVariable int id,
            @RequestPart("productJson") String productJson,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Product prod = objectMapper.readValue(productJson, Product.class);
            Product updatedProduct = service.updateProductById(id, prod, imageFile);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to update product: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/admin/product/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String > deleteProductById(@PathVariable int id)
    {
        Product product=service.getProductById(id);
        if (product !=null)
        {
            service.deleteProductById(id);
            return new ResponseEntity<>("Product is deleted complete",HttpStatus.OK);
        } else
        {
            return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        }

    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers()
    {
       List<User> users=service.getAllUsers();
       return new ResponseEntity<>(users,HttpStatus.OK);
    }
}