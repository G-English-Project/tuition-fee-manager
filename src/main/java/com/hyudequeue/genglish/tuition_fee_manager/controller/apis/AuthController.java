package com.hyudequeue.genglish.tuition_fee_manager.controller.apis;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request.UserAuthRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.UserAuthResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.AuthEndpoints.LOGIN_ENDPOINT;
import static com.hyudequeue.genglish.tuition_fee_manager.utility.constants.ApiPathConstant.AUTH_API;

@RestController
@RequiredArgsConstructor
@RequestMapping(AUTH_API)
public class AuthController {
    private final AuthService authService;

    @PostMapping(LOGIN_ENDPOINT)
    public ResponseEntity<UserAuthResponseDto> AuthorizeUser(@RequestBody UserAuthRequestDto user){
        return ResponseEntity.ok(authService.GetUserAuthorize(user));
    }

}
