package com.adarsh.Ecommerce.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService
{

    private final MyUserDetailsService UserDetailsService;

    public String generateToken(String username) {
        UserDetails userDetails=UserDetailsService.loadUserByUsername(username);
        List<String > roles=userDetails.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toList());

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles",roles);
        long expirationTime = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000;
        String token=Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(expirationTime))
                .and()
                .signWith(getKey())
                .compact();
        System.out.println("JWT Service - Generated Token: " + token);

        return token;
    }

    private SecretKey getKey() {
        String secKey = "Zm9vYmFyZm9vYmFyZm9vYmFyZm9vYmFyZm9vYmFyZm9vYmFyZm9vYmFyZm9vYmFy";
        byte[] keyBytes = Decoders.BASE64.decode(secKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", List.class));
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        boolean isValid = userName.equals(userDetails.getUsername()) && !isTokenExpired(token);
        System.out.println("JWT Service - Token Validation: " + isValid + " for " + userName);
        if (!isValid) {
            System.out.println("JWT Service - Token Expired: " + isTokenExpired(token));
        }
        return isValid;    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}