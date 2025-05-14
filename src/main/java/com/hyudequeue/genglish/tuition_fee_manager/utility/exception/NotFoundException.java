package com.hyudequeue.genglish.tuition_fee_manager.utility.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
  public NotFoundException(ApplicationErrorCode errorCode, Object... args) {
    this.errorCode = errorCode;
    this.args = args;
  }

  private final ApplicationErrorCode errorCode;

  private final Object[] args;
}
