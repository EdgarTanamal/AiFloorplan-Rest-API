package com.aifloorplan.aifloorplanrestapi.repository;

import org.springframework.data.repository.CrudRepository;

import com.aifloorplan.aifloorplanrestapi.model.Floorplan;

public interface FloorplanRepository extends CrudRepository<Floorplan, Integer> {
  public Iterable<Floorplan> findAllByChatIdChat(int id);
}