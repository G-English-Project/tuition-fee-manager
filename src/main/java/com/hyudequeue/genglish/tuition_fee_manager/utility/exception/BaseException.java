package com.hyudequeue.genglish.tuition_fee_manager.utility.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseException extends RuntimeException {

  private String code;

  private String message;
}
