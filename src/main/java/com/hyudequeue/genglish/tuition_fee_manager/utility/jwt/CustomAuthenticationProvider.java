package com.hyudequeue.genglish.tuition_fee_manager.utility.jwt;

import com.hyudequeue.genglish.tuition_fee_manager.model.custom.UserDetailsCustom;
import com.hyudequeue.genglish.tuition_fee_manager.model.entities.User;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.utility.Enums.RoleEnum;
import com.hyudequeue.genglish.tuition_fee_manager.utility.exception.ApplicationErrorCode;
import com.hyudequeue.genglish.tuition_fee_manager.utility.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    final String email = authentication.getPrincipal().toString();
    final String password = authentication.getCredentials().toString();
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () ->
                    new ApplicationException(
                        ApplicationErrorCode.USER_NOT_FOUND, "User not found"));

    if (!passwordEncoder.matches(password, user.getPasswordHash())) {
      throw new ApplicationException(ApplicationErrorCode.UNAUTHORIZED, "Invalid password");
    }

    List<RoleEnum> roles = List.of(user.getRole());

    UserDetailsCustom userDetails =
            new UserDetailsCustom(
                    user.getEmail(),
                    user.getPasswordHash(),
                    roles);
    return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider(
      UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder);
    return provider;
  }
}
