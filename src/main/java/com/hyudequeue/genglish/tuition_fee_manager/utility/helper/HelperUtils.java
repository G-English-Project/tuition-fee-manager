package com.hyudequeue.genglish.tuition_fee_manager.utility.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class HelperUtils {
    public static final ObjectWriter JSON_WRITER =
            new ObjectMapper().writer().withDefaultPrettyPrinter();
}
