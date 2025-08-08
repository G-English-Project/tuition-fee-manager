package com.hyudequeue.genglish.tuition_fee_manager.controller.apis;

import com.hyudequeue.genglish.tuition_fee_manager.service.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.hyudequeue.genglish.tuition_fee_manager.utility.constants.ApiPathConstants.PAYMENT_API;

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
