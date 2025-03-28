package com.adarsh.Ecommerce.repository;

import com.adarsh.Ecommerce.model.Cart;
import com.adarsh.Ecommerce.model.Product;
import com.adarsh.Ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long>
{
    @Query("SELECT c FROM Cart c JOIN FETCH c.product WHERE c.user = :user")
    List<Cart> findByUser( User user);

    @Query("SELECT c FROM Cart c JOIN FETCH c.product WHERE c.user = :user AND c.product = :product")
    Optional<Cart> findByUserAndProduct( User user, Product product);

    @Query("SELECT c FROM Cart c WHERE c.id = :cartId AND c.user = :user")
    Optional<Cart> findByIdAndUser(Long cartId,User user);

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.user = :user")
    void deleteByUser(User user);

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.product.productId = :productId")
    void deleteByProductId(@Param("productId") int productId);
}