package com.aifloorplan.aifloorplanrestapi.service;

import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aifloorplan.aifloorplanrestapi.dto.LoginRequest;
import com.aifloorplan.aifloorplanrestapi.dto.LoginResponse;
import com.aifloorplan.aifloorplanrestapi.model.User;
import com.aifloorplan.aifloorplanrestapi.repository.UserRepository;

@Service
public class AuthenticationService {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private AuthenticationManager authenticationManager;

  public LoginResponse register(LoginRequest request) throws Exception {
    // Cek email di database
    if (userRepository.findByEmailAndIsDeletedFalse(request.getEmail()).isPresent()) {
      throw new RuntimeException();
    }

    // Simpan email dan password (terenkripsi) di database
    User user = new User();
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    userRepository.save(user);

    User savedUser = userRepository.findByEmailAndIsDeletedFalse(request.getEmail()).orElseThrow();

    LoginResponse response = new LoginResponse(savedUser.getIdUser(), jwtService.generateToken(user));

    return response;
  }

  public LoginResponse authenticate(LoginRequest request) {
    authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    User user = userRepository.findByEmailAndIsDeletedFalse(request.getEmail()).orElseThrow();

    LoginResponse response = new LoginResponse(user.getIdUser(), jwtService.generateToken(user));

    return response;
  }

  public boolean isTokenValid(String authorization) {
    if (authorization == null || !authorization.startsWith("Bearer ")) {
      return false;
    }

    String token = authorization.substring(7);
    String email = jwtService.extractEmail(token);

    try {
      User user = userRepository.findByEmailAndIsDeletedFalse(email).orElseThrow();

      if (jwtService.isValid(token, user)) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null,
            user.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        return true;
      }

    } catch (NoSuchElementException e) {
      // Hanya untuk tangkap error biar sistem tetap berjalan
    }
    return false;
  }

  public boolean isRequestedIdAuthenticated(int id) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = (User) authentication.getPrincipal();
    return user.getIdUser() == id;
  }
}
