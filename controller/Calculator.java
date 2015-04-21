package controller;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import lotto.AbstractBonusBall;
import lotto.Lottery;
import analyzer.Algorithims;
import analyzer.GetDraws;

// TODO bad practice to have an agent class. Fix this.
/**
 * This class is an Agent class that is the entry point for computing the
 * results given Settings. From here, the computation is segregated between
 * games with a bonus ball and those without, each of which are processed
 * differently.
 * 
 * @author Robert
 */
public final class Calculator {

	private Calculator() {
		// prevent instantiation of this class
	}

	/**
	 * The entry point for computing a result.
	 * 
	 * @param settings
	 *            the settings to compute against
	 * @return a String result to display to the user
	 * @throws FileNotFoundException
	 *             if a database file cannot be opened
	 */
	public static String getResult(final Settings settings)
			throws FileNotFoundException {
		String result = "Internal Error in Calculator";
		if ((Boolean) settings.get(Settings.RAW_DATA)) {
			result = Algorithims.getRawData(settings.selectedGame(),
					createGetDraws(settings));
		} else {
			final Set<Integer> normalBallResults = processNormalGame(settings);
			final int ballsInGame = settings.selectedGame().getNumberOfBalls();
			if (settings.selectedGame().hasBonusBall()) {
				final Set<Integer> bonusBallResults = processBonusBallGame(settings);
				result = new Combinations(normalBallResults, bonusBallResults)
						.getOutput(
								(Boolean) settings.get(Settings.WHEEL_RESULTS),
								ballsInGame);
			} else {
				result = new Combinations(normalBallResults).getOutput(
						(Boolean) settings.get(Settings.WHEEL_RESULTS),
						ballsInGame);
			}
		}
		return result;
	}

	/**
	 * Compute results for a game without bonus balls with respect to the
	 * settings.
	 * 
	 * @param settings
	 *            the settings to compute with
	 * @return a set of integers for outputting
	 * @throws FileNotFoundException
	 *             if a database file cannot be opened
	 */
	private static Set<Integer> processNormalGame(final Settings settings)
			throws FileNotFoundException {
		final List<Integer> result = new LinkedList<Integer>(
				(Set<Integer>) settings.get(Settings.CUSTOM_NUMBERS));
		// remove too high of numbers from custom input
		removeNumbersHigherThan(result, settings.selectedGame().getRange());
		final GetDraws getDraws = createGetDraws(settings);
		if ((Boolean) settings.get(Settings.HOT_SELECTED)) {
			result.addAll(Algorithims.getHotNumbers(settings.selectedGame(),
					getDraws));
		}
		if ((Boolean) settings.get(Settings.COLD_SELECTED)) {
			result.addAll(Algorithims.getColdNumbers(settings.selectedGame(),
					getDraws));
		}
		if ((Boolean) settings.get(Settings.PERIODIC_SELECTED)) {
			result.addAll(Algorithims.getPeriodicNumbers(settings
					.selectedGame()));
		}
		if (!(Boolean) settings.get(Settings.INCLUSIVE)) {
			Collections.sort(result);
			for (int i = 0; i < result.size() - 1; i++) {
				if (result.get(i + 1) != result.get(i)) {
					result.remove(i);
					i--;
				}
			}
			if (!result.isEmpty()) {
				result.remove(result.size() - 1);
			}
		}
		return new TreeSet<Integer>(result);
	}

	/**
	 * Compute the numbers in a bonus ball game, except the bonus balls which
	 * will be added in later.
	 * 
	 * @param settings
	 *            the settings to run against
	 * @return the set of resulting normal numbers
	 * @throws FileNotFoundException
	 *             if a database file cannot be opened
	 */
	private static Set<Integer> processBonusBallGame(final Settings settings)
			throws FileNotFoundException {
		final List<Integer> result = new LinkedList<Integer>();
		final GetDraws getDraws = createGetDraws(settings);
		if ((Boolean) settings.get(Settings.HOT_SELECTED)) {
			result.addAll(Algorithims.getHotBonusBalls(
					(AbstractBonusBall) settings.selectedGame(), getDraws));
		}
		if ((Boolean) settings.get(Settings.COLD_SELECTED)) {
			result.addAll(Algorithims.getColdBonusBalls(
					(AbstractBonusBall) settings.selectedGame(), getDraws));
		}
		if ((Boolean) settings.get(Settings.PERIODIC_SELECTED)) {
			result.addAll(Algorithims
					.getPeriodicBonusBalls((AbstractBonusBall) settings
							.selectedGame()));
		}
		return new TreeSet<Integer>(result);
	}

	/**
	 * Given a list of custom numbers from the user, remove any numbers that
	 * exceed the range of the game.
	 * 
	 * @param list
	 *            the user input
	 * @param max
	 *            the max value, inclusive, for the game
	 */
	private static void removeNumbersHigherThan(final List<Integer> list,
			final int max) {
		final Iterator<Integer> itr = list.iterator();
		while (itr.hasNext()) {
			if (itr.next() > max) {
				itr.remove();
			}
		}
	}

	private static GetDraws createGetDraws(final Settings settings) {
		final Lottery game = settings.selectedGame();
		final int count = (Integer) settings.get(Settings.COUNT);
		final boolean jackpotOnly = (Boolean) settings
				.get(Settings.JACKPOT_ONLY);
		final long payout = (Long) settings.get(Settings.PAID_OVER_AMOUNT);
		return new GetDraws(game, count, jackpotOnly, payout);
	}
}
