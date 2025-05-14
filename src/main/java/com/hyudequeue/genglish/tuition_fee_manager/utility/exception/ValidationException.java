package com.hyudequeue.genglish.tuition_fee_manager.utility.exception;

import lombok.Getter;

import java.io.Serial;

@Getter
public class ValidationException extends RuntimeException {
  @Serial private static final long serialVersionUID = 8231722937707194721L;

  private final Object[] args;

  private final ApplicationErrorCode errorCode;

  public ValidationException(ApplicationErrorCode errorCode, Object... args) {
    this.args = args;
    this.errorCode = errorCode;
  }
}
