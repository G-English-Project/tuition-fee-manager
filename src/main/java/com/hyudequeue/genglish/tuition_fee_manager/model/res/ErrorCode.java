package com.hyudequeue.genglish.tuition_fee_manager.model.res;

import com.fasterxml.jackson.annotation.JsonValue;

public interface ErrorCode {
  Integer getHttpStatusCode();

  @JsonValue
  String getSystemCode();
}
