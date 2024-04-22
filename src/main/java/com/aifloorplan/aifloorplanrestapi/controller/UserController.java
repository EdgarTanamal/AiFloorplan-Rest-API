package com.aifloorplan.aifloorplanrestapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aifloorplan.aifloorplanrestapi.dto.ServerResponse;
import com.aifloorplan.aifloorplanrestapi.dto.UserRequest;
import com.aifloorplan.aifloorplanrestapi.model.User;
import com.aifloorplan.aifloorplanrestapi.service.AuthenticationService;
import com.aifloorplan.aifloorplanrestapi.service.ChatService;
import com.aifloorplan.aifloorplanrestapi.service.FloorplanService;
import com.aifloorplan.aifloorplanrestapi.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private ChatService chatService;

  @Autowired
  private FloorplanService floorplanService;

  @Autowired
  private AuthenticationService authenticationService;

  @GetMapping("/{userId}")
  public ResponseEntity<Object> getUserData(@PathVariable(value = "userId") int userId) {
    ServerResponse response = new ServerResponse();

    try {
      // Cek ketersediaan data user di database
      if (!userService.userExistsById(userId)) {
        response.addMessages("Data pengguna tidak tersedia atau tidak ditemukan");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      // Cek kesesuaian id user yang diminta dengan id user yang sedang terautentikasi
      if (!authenticationService.isRequestedIdAuthenticated(userId)) {
        response.addMessages("Hak izin dibutuhkan untuk mengakses endpoint");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
      }

      User user = userService.getUserById(userId);
      response.addPayloadField("email", user.getEmail());
      response.addPayloadField("premium", user.isPremium());
      response.setSuccess(true);

      return ResponseEntity.ok().body(response);
    } catch (Exception e) {
      response.addMessages("Terjadi kesalahan pada server");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @PutMapping("/{userId}")
  public ResponseEntity<Object> updateUserData(@PathVariable(value = "userId") int userId,
      @Valid @RequestBody UserRequest request, Errors errors) {
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
      if (!userService.userExistsById(userId)) {
        response.addMessages("Data pengguna tidak tersedia atau tidak ditemukan");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      // Cek kesesuaian id user yang diminta dengan id user yang sedang terautentikasi
      if (!authenticationService.isRequestedIdAuthenticated(userId)) {
        response.addMessages("Hak izin dibutuhkan untuk mengakses endpoint");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
      }

      userService.updateUserById(request, userId);

      response.setSuccess(true);
      response.addMessages("Data berhasil diperbarui");

      return ResponseEntity.ok().body(response);

    } catch (Exception e) {
      response.addMessages("Terjadi kesalahan pada server");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Object> deleteUserData(@PathVariable(value = "userId") int userId) {
    ServerResponse response = new ServerResponse();

    try {
      // Cek ketersediaan data user di database
      if (!userService.userExistsById(userId)) {
        response.addMessages("Data pengguna tidak tersedia atau tidak ditemukan");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      // Cek kesesuaian id user yang diminta dengan id user yang sedang terautentikasi
      if (!authenticationService.isRequestedIdAuthenticated(userId)) {
        response.addMessages("Hak izin dibutuhkan untuk mengakses endpoint");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
      }

      userService.deleteUserById(userId);

      response.setSuccess(true);
      response.addMessages("Data akun berhasil dihapus");

      return ResponseEntity.ok().body(response);

    } catch (Exception e) {
      response.addMessages("Terjadi kesalahan pada server");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @GetMapping("/{userId}/history")
  public ResponseEntity<Object> getUserHistory(@PathVariable(value = "userId") int userId) {
    ServerResponse response = new ServerResponse();

    try {
      // Cek ketersediaan data user di database
      if (!userService.userExistsById(userId)) {
        response.addMessages("Data pengguna tidak tersedia atau tidak ditemukan");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      // Cek kesesuaian id user yang diminta dengan id user yang sedang terautentikasi
      if (!authenticationService.isRequestedIdAuthenticated(userId)) {
        response.addMessages("Hak izin dibutuhkan untuk mengakses endpoint");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
      }

      response.addPayloadField("histories", chatService.getHistory(userId));
      response.setSuccess(true);

      return ResponseEntity.ok().body(response);

    } catch (Exception e) {
      response.addMessages("Terjadi kesalahan pada server");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @GetMapping("/{userId}/gallery")
  public ResponseEntity<Object> getUserGallery(@PathVariable(value = "userId") int userId) {
    ServerResponse response = new ServerResponse();

    try {
      // Cek ketersediaan data user di database
      if (!userService.userExistsById(userId)) {
        response.addMessages("Data pengguna tidak tersedia atau tidak ditemukan");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      // Cek kesesuaian id user yang diminta dengan id user yang sedang terautentikasi
      if (!authenticationService.isRequestedIdAuthenticated(userId)) {
        response.addMessages("Hak izin dibutuhkan untuk mengakses endpoint");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
      }

      response.addPayloadField("floorplans", floorplanService.getGalleryFloorplans(userId));
      response.setSuccess(true);

      return ResponseEntity.ok().body(response);

    } catch (Exception e) {
      response.addMessages("Terjadi kesalahan pada server");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @GetMapping("/{userId}/trash")
  public ResponseEntity<Object> getUserTrash(@PathVariable(value = "userId") int userId) {
    ServerResponse response = new ServerResponse();

    try {
      // Cek ketersediaan data user di database
      if (!userService.userExistsById(userId)) {
        response.addMessages("Data pengguna tidak tersedia atau tidak ditemukan");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      // Cek kesesuaian id user yang diminta dengan id user yang sedang terautentikasi
      if (!authenticationService.isRequestedIdAuthenticated(userId)) {
        response.addMessages("Hak izin dibutuhkan untuk mengakses endpoint");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
      }

      response.addPayloadField("floorplans", floorplanService.getTrashBinFloorplans(userId));
      response.setSuccess(true);

      return ResponseEntity.ok().body(response);

    } catch (Exception e) {
      response.addMessages("Terjadi kesalahan pada server");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @DeleteMapping("/{userId}/trash")
  public ResponseEntity<Object> deleteAllUserTrash(@PathVariable(value = "userId") int userId) {
    ServerResponse response = new ServerResponse();

    try {
      // Cek ketersediaan data user di database
      if (!userService.userExistsById(userId)) {
        response.addMessages("Data pengguna tidak tersedia atau tidak ditemukan");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      // Cek kesesuaian id user yang diminta dengan id user yang sedang terautentikasi
      if (!authenticationService.isRequestedIdAuthenticated(userId)) {
        response.addMessages("Hak izin dibutuhkan untuk mengakses endpoint");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
      }

      floorplanService.deleteAllFloorplan(userId);

      response.setSuccess(true);
      response.addMessages("Seluruh data floorplan berhasil dihapus dari trash bin");

      return ResponseEntity.ok().body(response);

    } catch (Exception e) {
      response.addMessages("Terjadi kesalahan pada server");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }
}
