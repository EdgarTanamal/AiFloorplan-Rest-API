package com.aifloorplan.aifloorplanrestapi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aifloorplan.aifloorplanrestapi.dto.ChatRequest;
import com.aifloorplan.aifloorplanrestapi.dto.ChatResponse;
import com.aifloorplan.aifloorplanrestapi.dto.ServerResponse;
import com.aifloorplan.aifloorplanrestapi.model.Chatgroup;
import com.aifloorplan.aifloorplanrestapi.service.AuthenticationService;
import com.aifloorplan.aifloorplanrestapi.service.ChatService;
import com.aifloorplan.aifloorplanrestapi.service.UserService;

import jakarta.validation.Valid;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

  @Autowired
  private UserService userService;

  @Autowired
  private ChatService chatService;

  @Autowired
  private AuthenticationService authenticationService;

  @PostMapping()
  public ResponseEntity<Object> createNewChat(
      @RequestHeader(value = "Authorization", required = false) String authorization,
      @Valid @RequestBody ChatRequest request, Errors errors) {
    ServerResponse response = new ServerResponse();

    try {
      // Validasi parameter
      if (errors.hasErrors()) {
        for (ObjectError error : errors.getAllErrors()) {
          response.addMessages(error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
      }

      // Cek autentikasi user
      if (request.getUserId() != 0 && !authenticationService.isTokenValid(authorization)) {
        response.addMessages("Autentikasi tidak valid");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
      }

      // Cek ketersediaan data user di database
      if (request.getUserId() != 0 && !userService.userExistsById(request.getUserId())) {
        response.addMessages("Data pengguna tidak tersedia atau tidak ditemukan");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      // Cek kesesuaian id user yang diminta dengan id user yang sedang terautentikasi
      if (request.getUserId() != 0 && !authenticationService.isRequestedIdAuthenticated(request.getUserId())) {
        response.addMessages("Hak izin tidak valid");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
      }

      // Cek ketersediaan data chatgroup di database
      if (request.getChatgroupId() != 0 && !chatService.chatgroupExistsById(request.getChatgroupId())) {
        response.addMessages("Data chatgroup tidak tersedia atau tidak ditemukan");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      ChatResponse chatResponse = chatService.createChat(request);
      response.addPayloadField("chat", chatResponse.getChat());
      response.addPayloadField("floorplans", chatResponse.getFloorplans());
      response.addPayloadField("chatgroupId", chatResponse.getChatgroupId());
      response.setSuccess(true);

      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (Exception e) {
      response.addMessages("Terjadi kesalahan pada server");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @GetMapping("/{chatgroupId}")
  public ResponseEntity<Object> getChatData(@PathVariable(value = "chatgroupId") int chatgroupId) {
    ServerResponse response = new ServerResponse();

    try {
      Optional<Chatgroup> chatgroup = chatService.getChatgroupById(chatgroupId);
      // Cek ketersediaan data chatgroup di database
      if (!chatgroup.isPresent()) {
        response.addMessages("Data chatgroup tidak tersedia atau tidak ditemukan");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      // Cek kesesuaian id user yang diminta dengan id user yang sedang terautentikasi
      if (!authenticationService.isRequestedIdAuthenticated(chatgroup.get().getUser().getIdUser())) {
        response.addMessages("Hak izin dibutuhkan untuk mengakses endpoint");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
      }

      response.addPayloadField("chatlist", chatService.getChatList(chatgroupId));
      response.setSuccess(true);

      return ResponseEntity.ok().body(response);
    } catch (Exception e) {
      response.addMessages("Terjadi kesalahan pada server");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @DeleteMapping("/{chatgroupId}")
  public ResponseEntity<Object> deleteChatData(@PathVariable(value = "chatgroupId") int chatgroupId) {
    ServerResponse response = new ServerResponse();

    try {
      Optional<Chatgroup> chatgroup = chatService.getChatgroupById(chatgroupId);
      // Cek ketersediaan data chatgroup di database
      if (!chatgroup.isPresent()) {
        response.addMessages("Data chatgroup tidak tersedia atau tidak ditemukan");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      // Cek kesesuaian id user yang diminta dengan id user yang sedang terautentikasi
      if (!authenticationService.isRequestedIdAuthenticated(chatgroup.get().getUser().getIdUser())) {
        response.addMessages("Hak izin dibutuhkan untuk mengakses endpoint");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
      }

      chatService.deleteChatgroupById(chatgroupId);

      response.setSuccess(true);
      response.addMessages("Data chatgroup berhasil dihapus");

      return ResponseEntity.ok().body(response);
    } catch (Exception e) {
      response.addMessages("Terjadi kesalahan pada server");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

}