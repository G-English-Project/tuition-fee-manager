package com.hyudequeue.genglish.tuition_fee_manager.utility.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {
  private String code;
  private String message;
}
