package com.hyudequeue.genglish.tuition_fee_manager.controller.apis;

import com.hyudequeue.genglish.tuition_fee_manager.service.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.hyudequeue.genglish.tuition_fee_manager.utility.constants.ApiPathConstant.AUTH_API;
import static com.hyudequeue.genglish.tuition_fee_manager.utility.constants.ApiPathConstant.INVOICE_API;

@RestController
@RequiredArgsConstructor
@RequestMapping(INVOICE_API)
public class InvoiceController {
    private final InvoiceService invoiceService;
}
