package com.aifloorplan.aifloorplanrestapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.aifloorplan.aifloorplanrestapi.dto.UserRequest;
import com.aifloorplan.aifloorplanrestapi.model.User;
import com.aifloorplan.aifloorplanrestapi.repository.UserRepository;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  public boolean userExistsById(int id) {
    return userRepository.existsByIdUserAndIsDeletedFalse(id);
  }

  public User getUserById(int id) {
    return userRepository.findByIdUserAndIsDeletedFalse(id).orElseThrow();
  }

  public void updateUserById(UserRequest request, int id) {
    User user = userRepository.findByIdUserAndIsDeletedFalse(id).orElseThrow();

    if (request.getPassword() != null && !request.getPassword().isEmpty()) {
      if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
        throw new NullPointerException("Kata sandi lama tidak valid");
      }

      if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new RuntimeException("Kata sandi baru tidak boleh sama dengan kata sandi lama");
      }

      user.setPassword(passwordEncoder.encode(request.getPassword()));
    }

    if (request.isPremium() != user.isPremium()) {
      user.setPremium(request.isPremium());
    }

    userRepository.save(user);
  }

  public void deleteUserById(int id) {
    User user = userRepository.findByIdUserAndIsDeletedFalse(id).orElseThrow();
    user.setDeleted(true);
    userRepository.save(user);
  }
}
