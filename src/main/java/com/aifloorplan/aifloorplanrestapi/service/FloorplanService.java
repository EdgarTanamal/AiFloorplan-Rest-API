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
        .findAllByUserIdUserAndIsDeletedFalseAndIsPermanentDeletedFalseOrderByCreateTimeDesc(id);
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
        .findAllByUserIdUserAndIsDeletedTrueAndIsPermanentDeletedFalseOrderByUpdateTimeAsc(id);
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

  public void removeFloorplan(List<Integer> floorplanIds) {
    Iterable<Gallery> galleries = galleryRepository
        .findByFloorplanIdFloorplanInAndIsDeletedFalseAndIsPermanentDeletedFalse(floorplanIds);
    for (Gallery gallery : galleries) {
      gallery.setDeleted(true);
    }
    galleryRepository.saveAll(galleries);
  }

  public void restoreFloorplan(List<Integer> floorplanIds) {
    Iterable<Gallery> galleries = galleryRepository
        .findByFloorplanIdFloorplanInAndIsDeletedTrueAndIsPermanentDeletedFalse(floorplanIds);
    for (Gallery gallery : galleries) {
      gallery.setDeleted(false);
    }
    galleryRepository.saveAll(galleries);
  }

  public void deleteFloorplan(List<Integer> floorplanIds) {
    Iterable<Gallery> galleries = galleryRepository
        .findByFloorplanIdFloorplanInAndIsDeletedTrueAndIsPermanentDeletedFalse(floorplanIds);
    for (Gallery gallery : galleries) {
      gallery.setPermanentDeleted(true);
    }
    galleryRepository.saveAll(galleries);
  }

  public void deleteAllFloorplan(int id) {
    Iterable<Gallery> galleries = galleryRepository
        .findAllByUserIdUserAndIsDeletedTrueAndIsPermanentDeletedFalseOrderByUpdateTimeAsc(id);
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
