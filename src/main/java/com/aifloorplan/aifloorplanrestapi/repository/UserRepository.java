package com.aifloorplan.aifloorplanrestapi.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.aifloorplan.aifloorplanrestapi.model.User;

public interface UserRepository extends CrudRepository<User, Integer> {

  public boolean existsByIdUserAndIsDeletedFalse(int id);

  public Optional<User> findByEmailAndIsDeletedFalse(String email);

  public Optional<User> findByIdUserAndIsDeletedFalse(int id);
}
