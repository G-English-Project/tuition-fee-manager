package com.hyudequeue.genglish.tuition_fee_manager.utility.exception;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.custom.BaseResponseDTO;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.HelperUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException {
    BaseResponseDTO<Object> responseDTO = new BaseResponseDTO<>();
    responseDTO.setMessage("You don't have permission to access this resource");
    responseDTO.setCode(String.valueOf(HttpStatus.FORBIDDEN.value()));

    String json = HelperUtils.JSON_WRITER.writeValueAsString(responseDTO);

    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(json);
  }
}
