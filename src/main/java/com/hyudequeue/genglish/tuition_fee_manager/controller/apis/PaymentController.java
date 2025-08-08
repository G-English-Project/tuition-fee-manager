package com.hyudequeue.genglish.tuition_fee_manager.controller.apis;

import com.hyudequeue.genglish.tuition_fee_manager.controller.res.ApiResp;
import com.hyudequeue.genglish.tuition_fee_manager.service.services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.payos.PayOS;
import vn.payos.type.PaymentData;

import static com.hyudequeue.genglish.tuition_fee_manager.controller.endpoints.ClassEndpoints.GET_ALL_CLASSES;
import static com.hyudequeue.genglish.tuition_fee_manager.utility.constants.ApiPathConstant.AUTH_API;
import static com.hyudequeue.genglish.tuition_fee_manager.utility.constants.ApiPathConstant.PAYMENT_API;

@RestController
@RequiredArgsConstructor
@RequestMapping(PAYMENT_API)
public class PaymentController {
    private final PaymentService paymentService;

//    @Operation(summary = "Get all payment", description = "Returns a paginated list of all classes.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "List of classes retrieved successfully"),
//    })
//    @GetMapping(GET_ALL_CLASSES)
//    public ResponseEntity<?> getAllClasses(){
//        PaymentData paymentDa
//        PayOS payOS = new PayOS();
//        return null;
//    }

}
