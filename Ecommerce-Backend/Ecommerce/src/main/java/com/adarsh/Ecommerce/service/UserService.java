package com.adarsh.Ecommerce.service;

import com.adarsh.Ecommerce.model.Product;
import com.adarsh.Ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final ProductRepository repo;

    public List<Product> getAllProducts()
    {
        return repo.findAll();
    }

    public Product getProductByIdForUser(int id)
    {
        return repo.findById(id).orElseThrow();
    }

    public List<Product> searchProducts(String keyword)
    {
        return repo.searchProducts(keyword);
    }

    public List<Product> getProductsByCategory(String category)
    {
        return repo.findByCategory(category);
    }
}
