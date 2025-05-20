package com.hyudequeue.genglish.tuition_fee_manager.utility.exception;

import com.hyudequeue.genglish.tuition_fee_manager.controller.res.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApplicationErrorCode implements ErrorCode {
  INVALID_REQUEST_PARAMETER("E_100_400_001", 400),
  INVALID_HTTP_REQUEST_METHOD("E_100_400_002", 400),
  INVALID_HTTP_REQUEST_RESOURCE("E_100_400_003", 400),

  UNAUTHORIZED("E_100_401_001", 401),
  INVALID_USERNAME_PASSWORD("E_100_401_002", 401),

  // Error Code 403
  FORBIDDEN("E_100_403_001", 403),

  // Error Code 404
  USER_NOT_FOUND("E_100_404_001", 404),

  ROOM_NOT_FOUND("E_100_404_001", 404),

  // Error Code 413
  MAXIMUM_PAY_LOAD("E_100_413_001", 413),

  // Error Code 500
  INTERNAL_ERROR_SERVER("E_100_500_001", 500);

  private final String systemCode;
  private final Integer httpStatusCode;
}
