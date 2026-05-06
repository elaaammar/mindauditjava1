package com.gestionaudit.utils;

import com.gestionaudit.MainFx;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tracks repeated inappropriate language. After strike 3, each new violation shows
 * five ControlsFX notifications in different screen positions warning of a 30s ban.
 */
public final class BadWordEnforcement {

    private static final AtomicInteger STRIKES = new AtomicInteger(0);

    private BadWordEnforcement() {}

    /**
     * If text contains a bad word, increments strikes, shows a warning notification,
     * and if strikes > 3 also shows five ban notifications in different places (30s each).
     *
     * @param text   user input
     * @param owner  window for anchoring notifications; may be null
     * @return true if the message must be blocked
     */
    public static boolean blockIfViolating(String text, javafx.stage.Window owner) {
        if (!BadWordFilter.containsBadWords(text)) {
            return false;
        }
        int n = STRIKES.incrementAndGet();
        String token = BadWordFilter.firstMatch(text).orElse("…");

        Platform.runLater(() -> {
            Notifications.create()
                    .owner(owner)
                    .title("Avertissement — langage")
                    .text("Mot inapproprié détecté (« " + token + " »). Avertissement n°" + n + ".")
                    .hideAfter(Duration.seconds(4))
                    .position(Pos.TOP_RIGHT)
                    .showWarning();
        });

        if (n > 3) {
            Platform.runLater(() -> showFiveBanNotifications(owner));
        }
        return true;
    }

    /**
     * Five ControlsFX notifications at different positions; each stays ~30s and states a 30s ban.
     */
    private static void showFiveBanNotifications(javafx.stage.Window owner) {
        /* Five distinct anchor points around the window */
        Pos[] positions = {
                Pos.TOP_LEFT,
                Pos.TOP_CENTER,
                Pos.TOP_RIGHT,
                Pos.BOTTOM_LEFT,
                Pos.BOTTOM_RIGHT
        };
        String[] lines = {
                "Dernière tentative : comportement inacceptable.",
                "Vous serez banni de l'envoi pendant 30 secondes.",
                "Sanction active : 30 s sans envoi de messages.",
                "Si cela continue, des mesures plus strictes suivront.",
                "Respectez la charte — pause 30 s imposée."
        };

        for (int i = 0; i < 5; i++) {
            final int idx = i;
            Notifications.create()
                    .owner(owner)
                    .title("Sanction — bannissement 30 s (" + (idx + 1) + "/5)")
                    .text(lines[idx])
                    .position(positions[idx])
                    .hideAfter(Duration.seconds(30))
                    .showError();
        }
    }

    public static void resetStrikes() {
        STRIKES.set(0);
    }

    public static int getStrikeCount() {
        return STRIKES.get();
    }
}
