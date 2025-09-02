package com.koco.guardian.util;

import com.koco.guardian.config.CustomUserDetail;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtUtil {

    // 🔑 Secret must be at least 32 chars for HS256
    private final String SECRET_KEY = "mysecret123mysecret123mysecret12345";

    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public String generateToken(UserDetails userDetails) {
        CustomUserDetail customUserDetail = (CustomUserDetail) userDetails;
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", userDetails.getAuthorities().stream()
                                             .map(GrantedAuthority::getAuthority).toList());
        String userId = customUserDetail.getAuthUser().getUserId();
        String role = customUserDetail.getAuthUser().getRole().getName();
        String type = customUserDetail.getAuthUser().getType().getName();
        claims.put("userId", userId);
        claims.put("role", role);
        claims.put("type", type);
        return Jwts.builder()
                   .setClaims(claims)
                   .setSubject(userDetails.getUsername())
                   .setIssuedAt(new Date(System.currentTimeMillis()))
                   .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                   .signWith(key, SignatureAlgorithm.HS256)
                   .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(key)
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }
    @SuppressWarnings("unchecked")
    public List<String> extractAuthorities(String token) {
        return (List<String>) extractAllClaims(token).get("authorities");
    }
}

