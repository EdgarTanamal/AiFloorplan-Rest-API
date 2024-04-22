package com.aifloorplan.aifloorplanrestapi.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.aifloorplan.aifloorplanrestapi.model.Chatgroup;

public interface ChatgroupRepository extends CrudRepository<Chatgroup, Integer> {

  public boolean existsByIdChatgroupAndIsDeletedFalse(int id);

  public Optional<Chatgroup> findByIdChatgroupAndIsDeletedFalse(int id);

  public Iterable<Chatgroup> findAllByUserIdUserAndIsDeletedFalse(int id);

}
