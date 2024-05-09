package com.aifloorplan.aifloorplanrestapi.controller;

import java.util.Optional;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aifloorplan.aifloorplanrestapi.dto.FloorplanRequest;
import com.aifloorplan.aifloorplanrestapi.dto.ServerResponse;
import com.aifloorplan.aifloorplanrestapi.model.Gallery;
import com.aifloorplan.aifloorplanrestapi.service.AuthenticationService;
import com.aifloorplan.aifloorplanrestapi.service.FloorplanService;
import com.aifloorplan.aifloorplanrestapi.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class FloorplanController {

  @Autowired
  private FloorplanService floorplanService;

  @Autowired
  private UserService userService;

  @Autowired
  private AuthenticationService authenticationService;

  @PostMapping("/gallery")
  public ResponseEntity<Object> saveToGallery(@Valid @RequestBody FloorplanRequest request, Errors errors) {
    ServerResponse response = new ServerResponse();

    try {
      // Validasi parameter
      if (errors.hasErrors()) {
        for (ObjectError error : errors.getAllErrors()) {
          response.addMessages(error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
      }

      // Cek ketersediaan data user di database
      if (!userService.userExistsById(request.getUserId())) {
        response.addMessages("Data pengguna tidak tersedia atau tidak ditemukan");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      // Cek kesesuaian id user yang diminta dengan id user yang sedang terautentikasi
      if (!authenticationService.isRequestedIdAuthenticated(request.getUserId())) {
        response.addMessages("Hak izin dibutuhkan untuk mengakses endpoint");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
      }

      // Cek ketersediaan data floorplan di database
      if (!floorplanService.floorplanExistsById(request.getFloorplanId())) {
        response.addMessages("Data floorplan tidak tersedia atau tidak ditemukan");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      // Cek ketersediaan data floorplan di trash bin
      if (floorplanService.floorplanExistsInTrashBin(request.getFloorplanId())) {
        response.addMessages("Data floorplan ditemukan di trash bin");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
      }

      // Cek ketersediaan data floorplan di gallery
      if (!floorplanService.floorplanExistsInGallery(request.getFloorplanId())) {
        floorplanService.saveFloorplan(request);
      }

      response.setSuccess(true);
      response.addMessages("Data floorplan berhasil disimpan");

      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (Exception e) {
      response.addMessages("Terjadi kesalahan pada server");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @DeleteMapping("/gallery")
  public ResponseEntity<Object> moveToTrashBin(@RequestParam(value = "floorplanIds") List<Integer> floorplanIds) {
    ServerResponse response = new ServerResponse();

    try {
      for (Integer floorplanId : floorplanIds) {
        Optional<Gallery> gallery = floorplanService.getGalleryFloorplan(floorplanId);
        // Cek ketersediaan data floorplan di database
        if (!gallery.isPresent()) {
          response.addMessages("Data floorplan dengan ID " + floorplanId + " tidak tersedia atau tidak ditemukan");
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Cek kesesuaian id user yang diminta dengan id user yang sedang terautentikasi
        if (!authenticationService.isRequestedIdAuthenticated(gallery.get().getUser().getIdUser())) {
          response.addMessages("Hak izin dibutuhkan untuk mengakses endpoint");
          return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
      }

      floorplanService.removeFloorplan(floorplanIds);

      response.setSuccess(true);
      response.addMessages("Data floorplan berhasil dipindahkan ke trash bin");

      return ResponseEntity.ok().body(response);
    } catch (Exception e) {
      response.addMessages("Terjadi kesalahan pada server");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @PutMapping("/trashbin")
  public ResponseEntity<Object> restoreToGallery(@RequestParam(value = "floorplanIds") List<Integer> floorplanIds) {
    ServerResponse response = new ServerResponse();

    try {
      for (Integer floorplanId : floorplanIds) {
        Optional<Gallery> gallery = floorplanService.getTrashBinFloorplan(floorplanId);
        // Cek ketersediaan data floorplan di database
        if (!gallery.isPresent()) {
          response.addMessages("Data floorplan dengan ID " + floorplanId + " tidak tersedia atau tidak ditemukan");
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Cek kesesuaian id user yang diminta dengan id user yang sedang terautentikasi
        if (!authenticationService.isRequestedIdAuthenticated(gallery.get().getUser().getIdUser())) {
          response.addMessages("Hak izin dibutuhkan untuk mengakses endpoint");
          return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
      }

      floorplanService.restoreFloorplan(floorplanIds);

      response.setSuccess(true);
      response.addMessages("Data floorplan berhasil dipulihkan");

      return ResponseEntity.ok().body(response);
    } catch (Exception e) {
      response.addMessages("Terjadi kesalahan pada server");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @DeleteMapping("/trashbin")
  public ResponseEntity<Object> deleteFromTrashBin(@RequestParam(value = "floorplanIds") List<Integer> floorplanIds) {
    ServerResponse response = new ServerResponse();

    try {
      for (Integer floorplanId : floorplanIds) {
        Optional<Gallery> gallery = floorplanService.getTrashBinFloorplan(floorplanId);
        // Cek ketersediaan data floorplan di database
        if (!gallery.isPresent()) {
          response.addMessages("Data floorplan dengan ID " + floorplanId + " tidak tersedia atau tidak ditemukan");
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Cek kesesuaian id user yang diminta dengan id user yang sedang terautentikasi
        if (!authenticationService.isRequestedIdAuthenticated(gallery.get().getUser().getIdUser())) {
          response.addMessages("Hak izin dibutuhkan untuk mengakses endpoint");
          return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
      }

      floorplanService.deleteFloorplan(floorplanIds);

      response.setSuccess(true);
      response.addMessages("Data floorplan berhasil dihapus dari trash bin");

      return ResponseEntity.ok().body(response);
    } catch (Exception e) {
      response.addMessages("Terjadi kesalahan pada server");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }
}