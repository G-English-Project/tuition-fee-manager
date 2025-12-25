package com.hyudequeue.genglish.tuition_fee_manager.controller.apis;

import com.hyudequeue.genglish.tuition_fee_manager.controller.model.payosconfig.response.PayOSConfigResponse;
import com.hyudequeue.genglish.tuition_fee_manager.controller.res.ApiResp;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.PayOSConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.PayOSConfigEndpoints.*;
import static com.hyudequeue.genglish.tuition_fee_manager.utility.constants.ApiPathConstants.PAYOS_CONFIG_API;

@RestController
@RequiredArgsConstructor
@RequestMapping(PAYOS_CONFIG_API)
public class PayOSConfigController {

    private final PayOSConfigService payOSConfigService;

    @Operation(summary = "Get PayOS config", description = "Get current active PayOS secret configuration.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Config retrieved successfully")
    })
    @GetMapping(GET_CONFIG)
    public ResponseEntity<ApiResp<PayOSConfigResponse>> getConfig() {
        return ApiResp.success(payOSConfigService.getConfig());
    }

    @Operation(summary = "Switch PayOS secret", description = "Switch between Payment Gate 1 and Payment Gate 2. Admin only.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Secret switched successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid secret number (must be 1 or 2)"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    })
    @PutMapping(SWITCH_SECRET)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResp<PayOSConfigResponse>> switchSecret(
            @Parameter(description = "Secret number (1 or 2)", required = true)
            @RequestParam Integer secretNumber) {
        return ApiResp.success(payOSConfigService.switchSecret(secretNumber));
    }
}

