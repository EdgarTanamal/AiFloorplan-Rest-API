package com.aifloorplan.aifloorplanrestapi.component;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.aifloorplan.aifloorplanrestapi.dto.ServerResponse;

@ControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

  // Class untuk atur response ketika autentikasi gagal atau tidak valid

  @ExceptionHandler({ AuthenticationException.class })
  @ResponseBody
  public ResponseEntity<Object> handleAuthenticationException(Exception ex) {
    ServerResponse response = new ServerResponse();
    response.addMessages("Autentikasi dibutuhkan untuk mengakses endpoint");
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
  }
}
