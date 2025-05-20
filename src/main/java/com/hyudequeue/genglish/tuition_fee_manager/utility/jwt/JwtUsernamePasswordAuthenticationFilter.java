package com.hyudequeue.genglish.tuition_fee_manager.utility.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyudequeue.genglish.tuition_fee_manager.config.JwtConfig;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.custom.UserDetailsCustom;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request.UserAuthRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.custom.BaseResponseDTO;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.utility.exception.ApplicationErrorCode;
import com.hyudequeue.genglish.tuition_fee_manager.utility.exception.NotFoundException;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.HelperUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.HashMap;
import java.util.stream.Collectors;

@Slf4j
public class JwtUsernamePasswordAuthenticationFilter
    extends AbstractAuthenticationProcessingFilter {

  private final JwtService jwtService;
  private final ObjectMapper objectMapper;
  private final UserRepository userRepository;

  public JwtUsernamePasswordAuthenticationFilter(
          AuthenticationManager manager, JwtConfig jwtConfig, JwtService jwtService, UserRepository userRepository) {
    super(new AntPathRequestMatcher(jwtConfig.getUrl(), "POST"));
    setAuthenticationManager(manager);
    this.objectMapper = new ObjectMapper();
    this.jwtService = jwtService;
    this.userRepository = userRepository;
  }

  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException, IOException {
    UserAuthRequestDto loginRequest = objectMapper.readValue(request.getInputStream(), UserAuthRequestDto.class);

    UsernamePasswordAuthenticationToken authRequest =
        new UsernamePasswordAuthenticationToken(
            loginRequest.getEmail(), loginRequest.getPassword());

    setDetails(request, authRequest);

    return this.getAuthenticationManager().authenticate(authRequest);
  }

  protected void setDetails(
      HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
    authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
  }

  @Override
  protected void successfulAuthentication(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication authentication)
      throws IOException {
    UserDetailsCustom userDetailsCustom = (UserDetailsCustom) authentication.getPrincipal();
    User user = userRepository.findByEmail(userDetailsCustom.getUsername())
            .orElseThrow(() -> new NotFoundException(ApplicationErrorCode.USER_NOT_FOUND));
    String accessToken = jwtService.generateToken(userDetailsCustom);
    BaseResponseDTO<Object> authResponse =
        BaseResponseDTO.builder()
            .code(String.valueOf(HttpStatus.OK.value()))
            .message("Authentication successful")
            .data(
                new HashMap<String, Object>() {
                  {
                    put("user_id", user.getUserId());
                    put("token", accessToken);
                    put("username", userDetailsCustom.getUsername());
                    put(
                        "roles",
                        userDetailsCustom.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList()));
                  }
                })
            .build();
    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(HttpStatus.OK.value());
    response.getWriter().write(objectMapper.writeValueAsString(authResponse));
  }

  @Override
  protected void unsuccessfulAuthentication(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
      throws IOException {
    BaseResponseDTO responseDTO = new BaseResponseDTO();
    responseDTO.setCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
    responseDTO.setMessage(failed.getLocalizedMessage());

    String json = HelperUtils.JSON_WRITER.writeValueAsString(responseDTO);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json; charset=UTF-8");
    response.getWriter().write(json);
    return;
  }
}
