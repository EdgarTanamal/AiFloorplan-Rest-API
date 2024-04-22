package com.aifloorplan.aifloorplanrestapi.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.aifloorplan.aifloorplanrestapi.model.User;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

@Service
public class JwtService {

  private String SECRET_KEY = "fa3393f28f4087f8a540000010b3f6bfc881181cbb6475978cce3bb1621d8f90";

  public String generateToken(User user) {
    String token = Jwts.builder().subject(user.getEmail()).issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)).signWith(getSignInKey()).compact();
    return token;
  }

  public boolean isValid(String token, UserDetails user) {
    String email = extractEmail(token);
    return (email.equals(user.getUsername())) && !isTokenExpired(token);
  }

  public <T> T extractClaim(String token, Function<Claims, T> resolver) {
    Claims claims = extractAllClaims(token);
    return resolver.apply(claims);
  }

  public String extractEmail(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
  }

  private SecretKey getSignInKey() {
    byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}