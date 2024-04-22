package com.aifloorplan.aifloorplanrestapi.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.aifloorplan.aifloorplanrestapi.model.Chat;

public interface ChatRepository extends CrudRepository<Chat, Integer> {
  Optional<Chat> findFirstByChatgroupIdChatgroup(int id);

  public Iterable<Chat> findAllByChatgroupIdChatgroup(int id);
}
