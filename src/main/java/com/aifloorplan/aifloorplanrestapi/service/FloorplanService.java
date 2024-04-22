package com.aifloorplan.aifloorplanrestapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aifloorplan.aifloorplanrestapi.dto.FloorplanRequest;
import com.aifloorplan.aifloorplanrestapi.dto.FloorplanResponse;
import com.aifloorplan.aifloorplanrestapi.model.Gallery;
import com.aifloorplan.aifloorplanrestapi.repository.FloorplanRepository;
import com.aifloorplan.aifloorplanrestapi.repository.GalleryRepository;
import com.aifloorplan.aifloorplanrestapi.repository.UserRepository;

@Service
public class FloorplanService {

  @Autowired
  private FloorplanRepository floorplanRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private GalleryRepository galleryRepository;

  public List<FloorplanResponse> getGalleryFloorplans(int id) {
    Iterable<Gallery> galleryList = galleryRepository
        .findAllByUserIdUserAndIsDeletedFalseAndIsPermanentDeletedFalse(id);
    List<FloorplanResponse> floorplans = new ArrayList<>();

    for (Gallery gallery : galleryList) {
      FloorplanResponse floorplan = new FloorplanResponse();
      floorplan.setFloorplanId(gallery.getFloorplan().getIdFloorplan());
      floorplan.setPrompt(gallery.getFloorplan().getPrompt());
      floorplan.setImageData(gallery.getFloorplan().getImageData());
      floorplan.setCreateTime(gallery.getCreateTime());

      floorplans.add(floorplan);
    }
    return floorplans;
  }

  public List<FloorplanResponse> getTrashBinFloorplans(int id) {
    Iterable<Gallery> galleryList = galleryRepository
        .findAllByUserIdUserAndIsDeletedTrueAndIsPermanentDeletedFalse(id);
    List<FloorplanResponse> floorplans = new ArrayList<>();

    for (Gallery gallery : galleryList) {
      FloorplanResponse floorplan = new FloorplanResponse();
      floorplan.setFloorplanId(gallery.getFloorplan().getIdFloorplan());
      floorplan.setPrompt(gallery.getFloorplan().getPrompt());
      floorplan.setImageData(gallery.getFloorplan().getImageData());
      floorplan.setCreateTime(gallery.getUpdateTime());

      floorplans.add(floorplan);
    }
    return floorplans;
  }

  public void saveFloorplan(FloorplanRequest request) {
    Gallery gallery = new Gallery();
    gallery.setUser(userRepository.findByIdUserAndIsDeletedFalse(request.getUserId()).orElseThrow());
    gallery.setFloorplan(floorplanRepository.findById(request.getFloorplanId()).orElseThrow());
    galleryRepository.save(gallery);
  }

  public void removeFloorplan(int id) {
    Gallery gallery = galleryRepository.findByFloorplanIdFloorplanAndIsDeletedFalseAndIsPermanentDeletedFalse(id)
        .orElseThrow();
    gallery.setDeleted(true);
    galleryRepository.save(gallery);
  }

  public void restoreFloorplan(int id) {
    Gallery gallery = galleryRepository.findByFloorplanIdFloorplanAndIsDeletedTrueAndIsPermanentDeletedFalse(id)
        .orElseThrow();
    gallery.setDeleted(false);
    galleryRepository.save(gallery);
  }

  public void deleteFloorplan(int id) {
    Gallery gallery = galleryRepository.findByFloorplanIdFloorplanAndIsDeletedTrueAndIsPermanentDeletedFalse(id)
        .orElseThrow();
    gallery.setPermanentDeleted(true);
    galleryRepository.save(gallery);
  }

  public void deleteAllFloorplan(int id) {
    Iterable<Gallery> galleries = galleryRepository.findAllByUserIdUserAndIsDeletedTrueAndIsPermanentDeletedFalse(id);
    for (Gallery gallery : galleries) {
      gallery.setPermanentDeleted(true);
    }
    galleryRepository.saveAll(galleries);
  }

  public boolean floorplanExistsById(int id) {
    return floorplanRepository.existsById(id);
  }

  public boolean floorplanExistsInGallery(int id) {
    return galleryRepository.existsByFloorplanIdFloorplanAndIsDeletedFalseAndIsPermanentDeletedFalse(id);
  }

  public boolean floorplanExistsInTrashBin(int id) {
    return galleryRepository.existsByFloorplanIdFloorplanAndIsDeletedTrueAndIsPermanentDeletedFalse(id);
  }

  public Optional<Gallery> getGalleryFloorplan(int id) {
    return galleryRepository.findByFloorplanIdFloorplanAndIsDeletedFalseAndIsPermanentDeletedFalse(id);
  }

  public Optional<Gallery> getTrashBinFloorplan(int id) {
    return galleryRepository.findByFloorplanIdFloorplanAndIsDeletedTrueAndIsPermanentDeletedFalse(id);
  }
}
