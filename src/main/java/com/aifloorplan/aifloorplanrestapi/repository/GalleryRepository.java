package com.aifloorplan.aifloorplanrestapi.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.aifloorplan.aifloorplanrestapi.model.Gallery;

public interface GalleryRepository extends CrudRepository<Gallery, Integer> {
  public boolean existsByFloorplanIdFloorplanAndIsDeletedFalseAndIsPermanentDeletedFalse(int id);

  public boolean existsByFloorplanIdFloorplanAndIsDeletedTrueAndIsPermanentDeletedFalse(int id);

  public Optional<Gallery> findByFloorplanIdFloorplanAndIsDeletedFalseAndIsPermanentDeletedFalse(int id);

  public Optional<Gallery> findByFloorplanIdFloorplanAndIsDeletedTrueAndIsPermanentDeletedFalse(int id);

  public Iterable<Gallery> findAllByUserIdUserAndIsDeletedFalseAndIsPermanentDeletedFalse(int id);

  public Iterable<Gallery> findAllByUserIdUserAndIsDeletedTrueAndIsPermanentDeletedFalse(int id);
}
