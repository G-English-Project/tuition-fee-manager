package com.hyudequeue.genglish.tuition_fee_manager.controller.apis;

import com.hyudequeue.genglish.tuition_fee_manager.service.services.ClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.hyudequeue.genglish.tuition_fee_manager.utility.constants.ApiPathConstant.AUTH_API;
import static com.hyudequeue.genglish.tuition_fee_manager.utility.constants.ApiPathConstant.CLASS_API;

@RestController
@RequiredArgsConstructor
@RequestMapping(CLASS_API)
public class ClassController {
    private final ClassService classService;
}
