package com.hyudequeue.genglish.tuition_fee_manager.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BaseResponseDTO<T> {
    private String code;

    private T data;

    private String message;
}
