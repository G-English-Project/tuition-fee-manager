package com.hyudequeue.genglish.tuition_fee_manager.controller.apis;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.request.UserAuthRequestDto;
import com.hyudequeue.genglish.tuition_fee_manager.controller.model.dtos.User.response.UserAuthResponseDto;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.AuthEndpoints.LOGIN_ENDPOINT;
import static com.hyudequeue.genglish.tuition_fee_manager.utility.constants.ApiPathConstant.AUTH_API;

@RestController
@RequiredArgsConstructor
@RequestMapping(AUTH_API)
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "User Login",
            description = "Authenticate user with credentials and receive a JWT token."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User authenticated successfully",
                    content = @Content(schema = @Schema(implementation = UserAuthResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid credentials",
                    content = @Content
            )
    })
    @PostMapping(LOGIN_ENDPOINT)
    public ResponseEntity<UserAuthResponseDto> AuthorizeUser(
            @RequestBody UserAuthRequestDto user
    ) {
        return ResponseEntity.ok(authService.GetUserAuthorize(user));
    }
}
