package com.adarsh.Ecommerce.service;

import com.adarsh.Ecommerce.model.User;
import com.adarsh.Ecommerce.model.UserPrincipal;
import com.adarsh.Ecommerce.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService
{

   private final AuthRepository repo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        User user=repo.findUserByUsername(username);
            if (user==null)
            {
                throw new UsernameNotFoundException("User not found");
            }
            return new UserPrincipal(user);

    }
}
