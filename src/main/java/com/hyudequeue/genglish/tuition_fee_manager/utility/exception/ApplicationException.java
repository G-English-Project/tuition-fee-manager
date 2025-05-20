package com.hyudequeue.genglish.tuition_fee_manager.utility.exception;

import com.hyudequeue.genglish.tuition_fee_manager.controller.res.ErrorCode;
import lombok.Getter;

/**
 * @author DigiEx
 */
@Getter
public class ApplicationException extends RuntimeException {

  private final ErrorCode errorCode;
  private final Object[] args;

  public ApplicationException(ErrorCode errorCode, Object... args) {
    this.errorCode = errorCode;
    this.args = args;
  }
}
