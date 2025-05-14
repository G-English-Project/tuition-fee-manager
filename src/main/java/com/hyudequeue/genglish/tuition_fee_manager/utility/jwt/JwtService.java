package com.hyudequeue.genglish.tuition_fee_manager.utility.jwt;

import com.hyudequeue.genglish.tuition_fee_manager.model.custom.UserDetailsCustom;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Collection;
import java.util.Map;
@Service
public interface JwtService {

  Claims extractClaims(String token);

  Key getKey();

  String generateToken(UserDetailsCustom userDetailsCustom);

  boolean isValidToken(String token);

  boolean isTokenExpired(String token);

}
