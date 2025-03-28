    package com.adarsh.Ecommerce.service;

    import com.adarsh.Ecommerce.dto.UserDto;
    import com.adarsh.Ecommerce.model.User;
    import com.adarsh.Ecommerce.repository.AuthRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.stereotype.Service;

    import java.util.HashMap;
    import java.util.Map;

    @Service
    @RequiredArgsConstructor
    public class AuthService
    {

        private final AuthRepository repo;
        private final AuthenticationManager authManager;
        private final JwtService jwtService;

        private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        public UserDto register(User user)
            {
                if (repo.findByUsername(user.getUsername()).isPresent())
                    {
                        throw new IllegalArgumentException(" Username already exists");
                    }

                if (user.getRole() == null || user.getRole().isEmpty())
                    {
                        user.setRole("ROLE_USER");
                    }
                user.setPassword(encoder.encode(user.getPassword()));
                User savedUser= repo.save(user);

                UserDto dto=new UserDto();
                dto.setId(savedUser.getId());
                dto.setUsername(savedUser.getUsername());
                dto.setRole(savedUser.getRole());
                return dto;
            }

        public Map<String, String> verify(User user) {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(user.getUsername());
                User dbUser = repo.findUserByUsername(user.getUsername());
                return new HashMap<String, String>() {{
                    put("token", token);
                    put("role", dbUser.getRole()); 
                }};
            }
            throw new IllegalArgumentException("Authentication failed");
        }
    }
