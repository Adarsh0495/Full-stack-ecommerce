package com.adarsh.Ecommerce.service;

import com.adarsh.Ecommerce.model.Product;
import com.adarsh.Ecommerce.model.User;
import com.adarsh.Ecommerce.repository.CartRepository;
import com.adarsh.Ecommerce.repository.ProductRepository;
import com.adarsh.Ecommerce.repository.UserRepository;
import com.cloudinary.Cloudinary;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService
{
     private final ProductRepository repo;
     private final UserRepository userRepository;
     private final CartRepository cartRepository;
     private final Cloudinary cloudinary;

    @Transactional
    public Product addProduct(Product prod) throws IOException
    {
        if (prod.getProductName()==null||prod.getProductName().isEmpty())
            {
                throw new IllegalArgumentException("Product name is required");
            }
        return repo.save(prod);
    }

    public String uploadImage(MultipartFile imageFile) throws IOException
    {
        Map uploadResult=cloudinary.uploader().upload(imageFile.getBytes(),
        Map.of("folder","ecommerce/products"));
        String imageurl=(String) uploadResult.get("secure_url");
        System.out.println("the url:"+imageurl);
        return imageurl;
    }

    @Cacheable(value = "products", key = "'all'")
    public List<Product> getAllProducts()
    {
       return repo.findAll();
    }

    @Cacheable(value = "products", key = "#id")
    public Product getProductById(int id)
    {
        return repo.findById(id).orElseThrow();
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public Product updateProductById(int id, Product prod, MultipartFile imageFile) throws IOException
    {
        Product existingProduct = getProductById(id);
        existingProduct.setProductName(prod.getProductName());
        existingProduct.setDescription(prod.getDescription());
        existingProduct.setCategory(prod.getCategory());
        existingProduct.setProductPrice(prod.getProductPrice());
        existingProduct.setQuantity(prod.getQuantity());

        if (imageFile != null && !imageFile.isEmpty())
        {
            if (existingProduct.getImage() != null && !existingProduct.getImage().isEmpty())
            {
                String publicId = extractPublicId(existingProduct.getImage());
                cloudinary.uploader().destroy(publicId, Map.of());
            }

            String imageUrl = uploadImage(imageFile);
            existingProduct.setImage(imageUrl);
        }
        return repo.save(existingProduct);
    }

    private String extractPublicId(String imageUrl)
    {
        String[] parts = imageUrl.split("/");
        String fileName = parts[parts.length - 1];
        return "ecommerce/products/" + fileName.substring(0, fileName.lastIndexOf("."));
    }

    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProductById(int id)
    {
        Product product=getProductById(id);
        String publicId=extractPublicId(product.getImage());
        try{
            cloudinary.uploader().destroy(publicId, Map.of());
            System.out.println("Deleted image from Cloudinary: " + publicId);
        }
        catch (IOException e) {
            System.err.println("Failed to delete image from Cloudinary: " + e.getMessage());
        }
        cartRepository.deleteByProductId(id);
        repo.delete(product);
        System.out.println("Product deleted: " + id);
    }

    @Cacheable(value = "users", key = "'all'")
    public List<User> getAllUsers()
    {
        return userRepository.findAll()
                .stream()
                .filter(user -> !"ROLE_ADMIN".equals(user.getRole()))
                .collect(Collectors.toList());
    }
}
