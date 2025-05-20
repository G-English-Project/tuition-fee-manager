package com.hyudequeue.genglish.tuition_fee_manager.controller.res;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Builder
public class ApiResp<T> {
  private boolean success;
  private T data;
  private ErrorResp error;

  public static <T> ResponseEntity<ApiResp<T>> success(T data) {
    return ResponseEntity.ok(ApiResp.<T>builder().success(true).data(data).build());
  }

  @Data
  @Builder
  public static class ErrorResp {
    private ErrorCode code;
    private String message;
    private Object details;
  }
}
