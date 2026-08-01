package com.hyudequeue.genglish.tuition_fee_manager.utility.helper;

public final class ActionLogDetail {

    private ActionLogDetail() {
    }

    /** Builds "key=value, key=value" from alternating keys and values. */
    public static String of(Object... keysAndValues) {
        if (keysAndValues == null || keysAndValues.length == 0) {
            return null;
        }

        StringBuilder detail = new StringBuilder();
        int pairCount = keysAndValues.length / 2;

        for (int i = 0; i < pairCount; i++) {
            if (i > 0) {
                detail.append(", ");
            }
            Object key = keysAndValues[i * 2];
            Object value = keysAndValues[i * 2 + 1];
            detail.append(key).append('=').append(value);
        }

        return detail.toString();
    }
}
