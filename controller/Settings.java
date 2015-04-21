package controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import lotto.Lottery;
import lotto.MegaMillions;
import lotto.PowerBall;
import lotto.WALotto;

/**
 * This class is used throughout the application in order to save the user's
 * settings that they want results to be computed against. The main component of
 * this class is a map from static Strings, used as keys, to Objects which then
 * need to be cast to be accessed. A few methods are also included here to help
 * parse through radio button settings where the possible values could be one of
 * many. This class is Serializable and that allows persistence between runs of
 * the application. If the serialized file cannot be deserialized, or doesn't
 * exist, a default settings object is provided as a constant.
 * 
 * @author Robert
 */
public final class Settings implements Serializable {

	/**
	 * For outputting Settings as a String.
	 */
	private static final char TAB = '\t';

	/**
	 * Microsoft new line for outputting Settings as a String.
	 */
	private static final String NEWLINE = "\r\n";

	/**
	 * It's a thing now.
	 */
	private static final long serialVersionUID = -8134471213507937669L;

	/**
	 * The default settings if Settings cannot be deserialized.
	 */
	public static final Settings DEFAULT = getDefaultSettings();

	/**
	 * Which game has been selected. Values are Strings.
	 */
	public static final String SELECTED_GAME = "Game";

	/**
	 * If algorithms' results are inclusive or exclusive. Values are Booleans.
	 */
	public static final String INCLUSIVE = "Inclusive";

	/**
	 * If Hot Numbers is selected as an algorithm. Values are Booleans.
	 */
	public static final String HOT_SELECTED = "Hot Numbers";

	/**
	 * If Cold Numbers is selected as an algorithm. Values are Booleans.
	 */
	public static final String COLD_SELECTED = "Cold Numbers";

	/**
	 * If Periodic Numbers is selected as an algorithm. Values are Booleans.
	 */
	public static final String PERIODIC_SELECTED = "Periodic Numbers";

	/**
	 * If only previous draws that paid out a jackpot are to be used, selected
	 * from the Menu. Values are Booleans.
	 */
	public static final String JACKPOT_ONLY = "Jackpot Only";

	/**
	 * If results are to be wheeled out. Values are Booleans.
	 */
	public static final String WHEEL_RESULTS = "Wheel Results";

	/**
	 * If the results are to be outputted as raw percentage data. Values are
	 * Booleans.
	 */
	public static final String RAW_DATA = "Output Raw Data";

	/**
	 * The number of previous draws to use. Values are Integers.
	 */
	public static final String COUNT = "Draws To Count";

	/**
	 * A payout amount, over which previous draws should be counted. Values are
	 * Longs.
	 */
	public static final String PAID_OVER_AMOUNT = "Paid Over";

	/**
	 * Custom numbers to always use from the user. Values are Set<Integer>
	 */
	public static final String CUSTOM_NUMBERS = "Custom Numbers";

	/**
	 * The main settings object.
	 */
	private final Map<String, Object> mySettings;

	/**
	 * This object must be constructed from DEFAULT, and can be recreated from a
	 * serialized file.
	 */
	private Settings() {
		mySettings = new HashMap<String, Object>();
	}

	/**
	 * Puts a value into the map for the given static key.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void put(final String key, final Object value) {
		mySettings.put(key, value);
	}

	/**
	 * Given a key, return its value. Null is returned if the key doesn't exist
	 * in the map.
	 * 
	 * @param key
	 *            the key to get the value for
	 * @return the value
	 */
	public Object get(final String key) {
		if (!mySettings.containsKey(key)) {
			return null;
		}
		return mySettings.get(key);
	}

	/**
	 * Return an instance of the game that is selected in these settings.
	 * 
	 * @return an instance of the selected game
	 */
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

	/**
	 * Return true if there is at least one algorithm selected. This must be
	 * true before results can be computed.
	 * 
	 * @return if an algorithm is selected
	 */
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

	/**
	 * Returns a string representation of these settings for display to the user
	 * in the final output.
	 * 
	 * @return string representation of these settings
	 */
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
		if ((Boolean) get(INCLUSIVE)) {
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

	/**
	 * Return an instance of Settings with default values.
	 * 
	 * @return a default settings object
	 */
	private static Settings getDefaultSettings() {
		final Settings defaultSettings = new Settings();
		defaultSettings.put(SELECTED_GAME, Lottery.WA_LOTTO);
		defaultSettings.put(INCLUSIVE, true);
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
