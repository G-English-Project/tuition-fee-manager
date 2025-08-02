package com.hyudequeue.genglish.tuition_fee_manager.service.basicOauth;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.custom.UserDetailsCustom;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.utility.exception.ApplicationErrorCode;
import com.hyudequeue.genglish.tuition_fee_manager.utility.exception.ApplicationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
@Service
public class UserDetailsServiceCustom implements UserDetailsService {

  private final UserRepository userRepository;

  public UserDetailsServiceCustom(UserRepository userRepository) {
    this.userRepository = userRepository;
  }
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserDetailsCustom userDetailsCustom = getUserDetailsCustom(username);
    if (ObjectUtils.isEmpty(userDetailsCustom)) {
      throw new ApplicationException(ApplicationErrorCode.USER_NOT_FOUND, "User not found");
    }
    return userDetailsCustom;
  }

  private UserDetailsCustom getUserDetailsCustom(String email) {
    return userRepository
            .findByEmail(email)
            .map(user ->
                    new UserDetailsCustom(
                            user.getEmail(),
                            user.getPasswordHash(),
                            user.getRole()
                    ))
            .orElseThrow(() ->
                    new ApplicationException(ApplicationErrorCode.USER_NOT_FOUND, "User not found"));
  }
}
