package com.kritim_mind.sms_project.utils;

public class SMSCalculator {
    private static final int GSM_7BIT_LENGTH = 160;
    private static final int GSM_7BIT_MULTIPART_LENGTH = 153;
    private static final int UNICODE_LENGTH = 70;
    private static final int UNICODE_MULTIPART_LENGTH = 67;

    public static int calculateSmsParts(String message) {
        if (message == null || message.isEmpty()) {
            return 0;
        }

        int length = message.length();
        boolean isUnicode = containsUnicodeCharacters(message);

        if (isUnicode) {
            if (length <= UNICODE_LENGTH) {
                return 1;
            }
            return (int) Math.ceil((double) length / UNICODE_MULTIPART_LENGTH);
        } else {
            if (length <= GSM_7BIT_LENGTH) {
                return 1;
            }
            return (int) Math.ceil((double) length / GSM_7BIT_MULTIPART_LENGTH);
        }
    }

    private static boolean containsUnicodeCharacters(String message) {
        String gsm7BitChars = "@£$¥èéùìòÇ\\nØø\\rÅåΔ_ΦΓΛΩΠΨΣΘΞÆæßÉ !\\\"#¤%&'()*+,-./0123456789:;<=>?" +
                "¡ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÑÜ§¿abcdefghijklmnopqrstuvwxyzäöñüà";

        for (char c : message.toCharArray()) {
            if (gsm7BitChars.indexOf(c) == -1) {
                return true;
            }
        }
        return false;
    }

}