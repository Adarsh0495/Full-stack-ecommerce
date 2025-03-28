package com.adarsh.Ecommerce.repository;

import com.adarsh.Ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<User,Integer>
{
    Optional<User> findByUsername(String username);

    User findUserByUsername(String username);
}


