package com.hyudequeue.genglish.tuition_fee_manager.utility.advice;

import com.hyudequeue.genglish.tuition_fee_manager.utility.exception.BaseException;
import com.hyudequeue.genglish.tuition_fee_manager.utility.exception.ExceptionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BaseException.class)
  public ResponseEntity<ExceptionResponse> handleBaseException(BaseException ex) {
    ExceptionResponse response = new ExceptionResponse(ex.getCode(), ex.getMessage());
    return ResponseEntity.status(Integer.parseInt(ex.getCode())).body(response);
  }
}
