package com.hyudequeue.genglish.tuition_fee_manager.utility.helper;

public class GenerateId {

    private static final int ID_LENGTH = 6; // số chữ số muốn pad

    public static String formatId(Long id) {
        if (id == null) return null;
        return String.format("%0" + ID_LENGTH + "d", id);
    }
}