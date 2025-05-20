package com.hyudequeue.genglish.tuition_fee_manager.utility.helper;

import java.security.SecureRandom;

public class PasswordUtils {

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()-_+=<>?";
    private static final String ALL = UPPER + LOWER + DIGITS + SPECIAL;

    private static final SecureRandom random = new SecureRandom();

    public static String generateRandomPassword(int minLength, int maxLength) {
        int length = random.nextInt(maxLength - minLength + 1) + minLength;
        StringBuilder password = new StringBuilder(length);

        password.append(getRandomChar(UPPER));
        password.append(getRandomChar(LOWER));
        password.append(getRandomChar(DIGITS));
        password.append(getRandomChar(SPECIAL));

        for (int i = 4; i < length; i++) {
            password.append(getRandomChar(ALL));
        }

        return shuffleString(password.toString());
    }

    private static char getRandomChar(String chars) {
        return chars.charAt(random.nextInt(chars.length()));
    }

    private static String shuffleString(String input) {
        char[] array = input.toCharArray();
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char tmp = array[i];
            array[i] = array[j];
            array[j] = tmp;
        }
        return new String(array);
    }
}
