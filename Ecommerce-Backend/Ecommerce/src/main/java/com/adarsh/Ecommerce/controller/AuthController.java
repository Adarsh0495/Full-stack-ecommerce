package com.adarsh.Ecommerce.controller;

import com.adarsh.Ecommerce.dto.UserDto;
import com.adarsh.Ecommerce.model.User;
import com.adarsh.Ecommerce.service.AuthService;
import com.adarsh.Ecommerce.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;
    private final TokenBlacklistService tokenBlacklistService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user)
    {
        try
            {
                UserDto savedUser= service.register(user);
                return new ResponseEntity<>(savedUser,HttpStatus.OK);
            }
        catch (IllegalArgumentException e)
        {
            return new ResponseEntity<>(Collections.singletonMap("error", e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestBody User user)
    {
        try
            {
                Map<String ,String> response=service.verify(user);
               return ResponseEntity.ok(response);
            }
        catch (IllegalArgumentException e)
            {
                return new ResponseEntity<>(e.getMessage(),HttpStatus.UNAUTHORIZED);
            }
    }


    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader String authHeader) {
        Map<String, String> response = new HashMap<>();
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("Blacklisting token: " + token);
            tokenBlacklistService.blacklistedTokens(token);
            response.put("message", "Logged out successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        response.put("message", "No valid token provided");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }




}
