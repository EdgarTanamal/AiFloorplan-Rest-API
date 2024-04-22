package com.aifloorplan.aifloorplanrestapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.aifloorplan.aifloorplanrestapi.repository.UserRepository;

@Service
public class UserDetailsServiceImp implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByEmailAndIsDeletedFalse(username)
        .orElseThrow(() -> new UsernameNotFoundException("User tidak ditemukan"));
  }
}
