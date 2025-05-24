package com.hyudequeue.genglish.tuition_fee_manager.utility.jwt;

import com.hyudequeue.genglish.tuition_fee_manager.config.JwtConfig;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.custom.UserDetailsCustom;
import com.hyudequeue.genglish.tuition_fee_manager.entities.Enums.RoleEnum;
import com.hyudequeue.genglish.tuition_fee_manager.utility.exception.BaseException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {

  private final JwtConfig jwtConfig;
  private final UserDetailsService userDetailsService;
//  private final TokenBlacklistService tokenBlacklistService;

  @Override
  public Claims extractClaims(String token) {
    return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
  }

  @Override
  public SecretKey getKey() {
    byte[] key = Decoders.BASE64.decode(jwtConfig.getSecret());
    return Keys.hmacShaKeyFor(key);
  }

  @Override
  public String generateToken(UserDetailsCustom userDetailsCustom) {
    Instant now = Instant.now();

    List<RoleEnum> roles = userDetailsCustom.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .map(auth -> auth.replace("ROLE_", ""))
            .map(RoleEnum::valueOf)
            .collect(Collectors.toList());

    log.info("Roles: {} ", roles);

    return Jwts.builder()
            .subject(userDetailsCustom.getUsername())
            .claim("roles", roles)
            .claim("isEnable", userDetailsCustom.isEnabled())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(jwtConfig.getExpiration())))
            .signWith(getKey())
            .compact();
  }
  @Override
  public boolean isValidToken(String token) {
    try {
      final String email = extractEmail(token);
      log.info("Username: {}", email);
      UserDetails userDetails = userDetailsService.loadUserByUsername(email);
      log.info("User details: {}", userDetails);

      List<String> roles = extractClaims(token, claims -> claims.get("roles", List.class));

      if (roles == null || roles.isEmpty()) {
        return false;
      }

      List<RoleEnum> roleEnums = roles.stream()
              .map(RoleEnum::valueOf)
              .collect(Collectors.toList());

      return !ObjectUtils.isEmpty(userDetails)
              && !isTokenExpired(token);
//              && !tokenBlacklistService.isTokenBlacklisted(token);
    } catch (Exception e) {
      log.info("The token is invalid because: {}", e.getMessage());
      return false;
    }
  }



  private String extractEmail(String token) {
    return extractClaims(token, Claims::getSubject);
  }

  private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
    final Claims claims = extractAllClaims(token);
    return claimsTFunction.apply(claims);
  }


  private Claims extractAllClaims(String token) {
    Claims claims = null;

    try {
      claims = Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
    } catch (ExpiredJwtException e) {
      throw new BaseException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), "Token expiration");
    } catch (UnsupportedJwtException e) {
      throw new BaseException(
          String.valueOf(HttpStatus.UNAUTHORIZED.value()), "Token's not supported");
    } catch (MalformedJwtException e) {
      throw new BaseException(
          String.valueOf(HttpStatus.UNAUTHORIZED.value()), "Invalid format 3 part of token");
    } catch (SignatureException e) {
      throw new BaseException(
          String.valueOf(HttpStatus.UNAUTHORIZED.value()), "Invalid format token");
    } catch (Exception e) {
      throw new BaseException(
          String.valueOf(HttpStatus.UNAUTHORIZED.value()), e.getLocalizedMessage());
    }

    return claims;
  }

  public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  public Date extractExpiration(String token) {
    return extractClaims(token, Claims::getExpiration);
  }
}
