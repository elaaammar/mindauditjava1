package com.gestionaudit.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class BadWordFilter {
    /** Demo / policy list — extend or load from config as needed. */
    private static final List<String> BAD_WORDS = Arrays.asList(
            "insulte1", "insulte2", "badword", "merde", "salaud"
    );

    public static boolean containsBadWords(String text) {
        return firstMatch(text).isPresent();
    }

    /** First offending token found in {@code text}, if any. */
    public static Optional<String> firstMatch(String text) {
        if (text == null || text.isBlank()) {
            return Optional.empty();
        }
        String lowerText = text.toLowerCase(Locale.ROOT);
        for (String word : BAD_WORDS) {
            String w = word.toLowerCase(Locale.ROOT);
            if (lowerText.contains(w)) {
                return Optional.of(word);
            }
        }
        return Optional.empty();
    }
}
