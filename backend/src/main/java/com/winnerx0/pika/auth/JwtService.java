package com.winnerx0.pika.auth;

import com.winnerx0.pika.users.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class JwtService {

    @Value("${jwt.secret-key}")
    private String secretKey;

    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    private Claims extractAllClaims(String token){

        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver){

        Claims claims = extractAllClaims(token);

        return claimResolver.apply(claims);
    }

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractSubject(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public String generateRefreshToken(User user){
        return buildToken(new HashMap<>(), user, 7 * 24 * 60 * 60 * 1000);
    }

    public String generateAccessToken(Map<String, Object> claims, User user){
        return buildToken(claims, user, 30 * 60 * 1000);
    }

    public boolean isTokenValid(String token, String subject){
        return extractExpiration(token).after(new Date()) && extractSubject(token).equals(subject);
    }

    private String buildToken(Map<String, Object> claims, User user, long expiration){

        return Jwts.builder()
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .subject(user.getUsername())
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .compact();
    }



}
