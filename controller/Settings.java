
package controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import lotto.Lottery;
import lotto.MegaMillions;
import lotto.PowerBall;
import lotto.WALotto;

public final class Settings implements Serializable {
    
    private static final char TAB = '\t';

    private static final String NEWLINE = "\r\n";

    /**
     * It's a thing now.
     */
    private static final long serialVersionUID = -8134471213507937669L;

    public static final Settings DEFAULT = getDefaultSettings();

    public static final String SELECTED_GAME = "Game";
    public static final String ADD_RESULTS = "Add Results";
    public static final String HOT_SELECTED = "Hot Numbers";
    public static final String COLD_SELECTED = "Cold Numbers";
    public static final String PERIODIC_SELECTED = "Periodic Numbers";
    public static final String JACKPOT_ONLY = "Jackpot Only";
    public static final String WHEEL_RESULTS = "Wheel Results";
    public static final String RAW_DATA = "Output Raw Data";
    public static final String COUNT = "Draws To Count";
    public static final String PAID_OVER_AMOUNT = "Paid Over";
    public static final String CUSTOM_NUMBERS = "Custom Numbers";
    
    private final Map<String, Object> mySettings;

    private Settings() {
        mySettings = new HashMap<String, Object>();
    }

    public void put(final String theKey, final Object theValue) {
        mySettings.put(theKey, theValue);
    }
    
    public Object get(final String theKey) {
        return mySettings.get(theKey);
    }
    
    public Lottery selectedGame() {
        final String gameValue = (String) get(SELECTED_GAME);
        Lottery game = null;
        if (gameValue.equals(Lottery.WA_LOTTO)) {
            game = new WALotto();
        } else if (gameValue.equals(Lottery.MEGA_MILLIONS)) {
            game = new MegaMillions();
        } else if (gameValue.equals(Lottery.POWERBALL)) {
            game = new PowerBall();
        }
        return game;
    }

    public boolean anAlgorithmSelected() {
        boolean result = false;
        if ((Boolean) mySettings.get(HOT_SELECTED)) {
            result = true;
        } else if ((Boolean) mySettings.get(COLD_SELECTED)) {
            result = true;
        } else if ((Boolean) mySettings.get(PERIODIC_SELECTED)) {
            result = true;
        }
        return result;
    }
    
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Game Selected:       ");
        builder.append(get(SELECTED_GAME));
        builder.append(NEWLINE);
        builder.append("Algorithms Selected: ");
        if ((Boolean) get(HOT_SELECTED)) {
            builder.append(NEWLINE);
            builder.append(TAB);
            builder.append(HOT_SELECTED);
        }
        if ((Boolean) get(COLD_SELECTED)) {
            builder.append(NEWLINE);
            builder.append(TAB);
            builder.append(COLD_SELECTED);
        }
        if ((Boolean) get(PERIODIC_SELECTED)) {
            builder.append(NEWLINE);
            builder.append(TAB);
            builder.append(PERIODIC_SELECTED);
        }
        builder.append(NEWLINE);
        builder.append("Algorithms Should:   ");
        if ((Boolean) get(ADD_RESULTS)) {
            builder.append("Add");
        } else {
            builder.append("Subtract");
        }
        builder.append(NEWLINE);
        builder.append("Draws to Count:      "); 
        builder.append(get(COUNT));
        builder.append(NEWLINE);
        builder.append("Your Lucky numbers:  ");
        builder.append(get(CUSTOM_NUMBERS));
        builder.append(NEWLINE);
        if ((Long) get(PAID_OVER_AMOUNT) > 0) {
            builder.append("Use Drawings that paid: $");
            builder.append(get(PAID_OVER_AMOUNT));
            builder.append(NEWLINE);
        }
        if ((Boolean) get(JACKPOT_ONLY)) {
            builder.append("Use Only Jackpot Winning Drawings");
            builder.append(NEWLINE);
        }
        return builder.toString();
    }
    
    private static Settings getDefaultSettings() {
        final Settings defaultSettings = new Settings();
        defaultSettings.put(SELECTED_GAME, Lottery.WA_LOTTO);
        defaultSettings.put(ADD_RESULTS, true);
        defaultSettings.put(HOT_SELECTED, false);
        defaultSettings.put(COLD_SELECTED, false);
        defaultSettings.put(PERIODIC_SELECTED, false);
        defaultSettings.put(JACKPOT_ONLY, false);
        defaultSettings.put(WHEEL_RESULTS, true);
        defaultSettings.put(RAW_DATA, false);
        defaultSettings.put(COUNT, 0);
        defaultSettings.put(PAID_OVER_AMOUNT, 0L);
        defaultSettings.put(CUSTOM_NUMBERS, new HashSet<Integer>());
        return defaultSettings;
    }

}
