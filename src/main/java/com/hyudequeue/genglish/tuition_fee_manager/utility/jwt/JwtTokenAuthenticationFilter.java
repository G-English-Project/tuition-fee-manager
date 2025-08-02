package com.hyudequeue.genglish.tuition_fee_manager.utility.jwt;

import com.hyudequeue.genglish.tuition_fee_manager.config.JwtConfig;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.custom.BaseResponseDTO;
import com.hyudequeue.genglish.tuition_fee_manager.utility.helper.HelperUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

  private final JwtConfig jwtConfig;

  private final JwtService jwtService;

  public JwtTokenAuthenticationFilter(JwtConfig jwtConfig, JwtService jwtService) {
    this.jwtConfig = jwtConfig;
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String accessToken = request.getHeader(jwtConfig.getHeader());

    if (!ObjectUtils.isEmpty(accessToken) && accessToken.startsWith(jwtConfig.getPrefix() + " ")) {
      accessToken = accessToken.substring((jwtConfig.getPrefix() + " ").length());
      try {
        if (jwtService.isValidToken(accessToken)) {
          Claims claims = jwtService.extractClaims(accessToken);

          String email = claims.getSubject();

          List<String> authorities = claims.get("roles", List.class);

          if (!ObjectUtils.isEmpty(email)) {
            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        email,
                    null,
                    authorities.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList()));
            SecurityContextHolder.getContext().setAuthentication(auth);
          }
        } else {
          log.info("Failed the valid token test");
        }
      } catch (Exception e) {
        log.error(
            "Error on filter once per request, path {}, error: {}",
            request.getRequestURI(),
            e.getMessage());
        log.warn(
            "Error on filter once per request, path {}, error: {}",
            request.getRequestURI(),
            e.getMessage());
        BaseResponseDTO<ObjectUtils> responseDTO = new BaseResponseDTO();
        responseDTO.setCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
        responseDTO.setMessage(e.getLocalizedMessage());

        String json = HelperUtils.JSON_WRITER.writeValueAsString(responseDTO);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(json);
        return;
      }
    }
    filterChain.doFilter(request, response);
  }
}
