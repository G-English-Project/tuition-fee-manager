package com.hyudequeue.genglish.tuition_fee_manager.service.servicesImpl;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.hyudequeue.genglish.tuition_fee_manager.config.JwtConfig;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.custom.UserDetailsCustom;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request.UserAuthRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.UserAuthResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.UserResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.entities.User;
import com.hyudequeue.genglish.tuition_fee_manager.repository.UserRepository;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.AuthService;
import com.hyudequeue.genglish.tuition_fee_manager.utility.jwt.JwtService;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
@Service
public class AuthServiceImpl implements AuthService {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;

    public AuthServiceImpl(
            JwtService jwtService,
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            JwtConfig jwtConfig) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtConfig = jwtConfig;
    }

    @Override
    public UserAuthResponseDto GetUserAuthorize(UserAuthRequestDto userRequest) {
        User user =
                userRepository
                        .findByEmail(userRequest.getEmail())
                        .orElseThrow(() ->
                                new ResponseStatusException(HttpStatusCode.valueOf(404), "User not found"));
        if (user.getPasswordHash() == null) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(401), "Incorrect login method, password is not initialized here yet");
        }
        if (!BCrypt.verifyer().verify(userRequest.getPassword().toCharArray(), user.getPasswordHash()).verified) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(403), "Incorrect password");
        }

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                userRequest.getEmail(), userRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtService.generateToken((UserDetailsCustom) authentication.getPrincipal());

        return new UserAuthResponseDto(token, jwtConfig.getPrefix(), UserResponseDto.toDto(user));
    }
}
