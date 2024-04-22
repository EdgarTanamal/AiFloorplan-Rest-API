package com.aifloorplan.aifloorplanrestapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import com.aifloorplan.aifloorplanrestapi.dto.LoginRequest;
import com.aifloorplan.aifloorplanrestapi.dto.LoginResponse;
import com.aifloorplan.aifloorplanrestapi.dto.ServerResponse;
import com.aifloorplan.aifloorplanrestapi.service.AuthenticationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class AuthenticationController {
  @Autowired
  private AuthenticationService authenticationService;

  @PostMapping("/register")
  public ResponseEntity<Object> registerUser(@Valid @RequestBody LoginRequest request, Errors errors) {
    ServerResponse response = new ServerResponse();

    try {
      // Validasi parameter
      if (errors.hasErrors()) {
        for (ObjectError error : errors.getAllErrors()) {
          response.addMessages(error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
      }

      LoginResponse payload = authenticationService.register(request);

      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + payload.getToken());

      response.addPayloadField("userId", payload.getUserId());
      response.addMessages("Registrasi berhasil");
      response.setSuccess(true);

      return ResponseEntity.status(HttpStatus.CREATED).headers(headers).body(response);

    } catch (RuntimeException e) {
      response.addMessages("Email yang digunakan sudah terdaftar dengan akun lain");
      return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    } catch (Exception e) {
      response.addMessages("Terjadi kesalahan pada server");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @PostMapping("/login")
  public ResponseEntity<Object> loginUser(@Valid @RequestBody LoginRequest request, Errors errors) {
    ServerResponse response = new ServerResponse();

    try {
      // Validasi parameter
      if (errors.hasErrors()) {
        for (ObjectError error : errors.getAllErrors()) {
          response.addMessages(error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
      }

      LoginResponse payload = authenticationService.authenticate(request);

      HttpHeaders headers = new HttpHeaders();
      headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + payload.getToken());

      response.addPayloadField("userId", payload.getUserId());
      response.addMessages("Login berhasil");
      response.setSuccess(true);

      return ResponseEntity.ok().headers(headers).body(response);

    } catch (AuthenticationException e) {
      response.addMessages("Email atau kata sandi yang diberikan salah");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    } catch (Exception e) {
      response.addMessages("Terjadi kesalahan pada server");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }
}