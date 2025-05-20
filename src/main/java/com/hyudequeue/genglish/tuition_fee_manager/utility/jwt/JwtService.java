package com.hyudequeue.genglish.tuition_fee_manager.utility.jwt;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.custom.UserDetailsCustom;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.security.Key;

@Service
public interface JwtService {

  Claims extractClaims(String token);

  Key getKey();

  String generateToken(UserDetailsCustom userDetailsCustom);

  boolean isValidToken(String token);

  boolean isTokenExpired(String token);

}
