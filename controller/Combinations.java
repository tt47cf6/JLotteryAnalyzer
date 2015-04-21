package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class recursively generates a wheeling of a given size of given
 * myNumberSet.
 * 
 * @author Robert
 */
public final class Combinations {
	/**
	 * Space character for inbetween numbers on same line.
	 */
	private static final char SPACE = ' ';
	/**
	 * Newline specific for Windows machines.
	 */
	private static final String MS_NEWLINE = "\r\n";
	/**
	 * The numbers to wheel.
	 */
	private final int[] myNumbersArray;
	/**
	 * Bonus balls to wheel.
	 */
	private int[] myBonusBallArray;
	/**
	 * If repetition of numbers is allowed.
	 */
	private boolean myRepititionFlag;
	/**
	 * If bonus balls need to be added.
	 */
	private boolean myBonusBallFlag;

	/**
	 * This constructor takes in an Integer Set and creates an int[] using it
	 * for direct use in this. Sets repetition to its default value, false.
	 * 
	 * @param numbers
	 *            an Integer Set to create an int[] with
	 */
	public Combinations(final Set<Integer> numbers) {
		myRepititionFlag = false;
		myBonusBallFlag = false;
		// convert set to array
		myNumbersArray = new int[numbers.size()];
		final Iterator<Integer> itr = numbers.iterator();
		int i = 0;
		while (itr.hasNext()) {
			myNumbersArray[i] = itr.next();
			i++;
		}
		Arrays.sort(myNumbersArray);
	}

	/**
	 * Creates a wheeling object with bonus balls.
	 * 
	 * @param numbers
	 *            the numbers to wheel
	 * @param bonusBalls
	 *            the bonus balls to add to the end
	 */
	public Combinations(final Set<Integer> numbers,
			final Set<Integer> bonusBalls) {
		this(numbers);
		myBonusBallArray = new int[bonusBalls.size()];
		myBonusBallFlag = true;
		final Iterator<Integer> itrB = bonusBalls.iterator();
		int iB = 0;
		while (itrB.hasNext()) {
			myBonusBallArray[iB] = itrB.next();
			iB++;
		}
		Arrays.sort(myBonusBallArray);
	}

	/**
	 * Sets myRepititionFlag to the given value.
	 * 
	 * @param allowRepFlag
	 *            the value to set to myRepititionFlag
	 */
	public void setRepitition(final boolean allowRepFlag) {
		myRepititionFlag = allowRepFlag;
	}

	/**
	 * A helper method for the recursive method that creates the wheeling.
	 * Throws an IllegalStateException if the size is less than the size of the
	 * int[] myNumberSet.
	 * 
	 * @param k
	 *            the number of myNumberSet in the game
	 * @return the wheeling of myNumberSet
	 */
	private List<Set<Integer>> getCombos(final int k) {
		if (k > myNumbersArray.length || k <= 0) {
			throw new IllegalArgumentException();
		}
		return getCombos(k, 0, new HashSet<Integer>());
	}

	/**
	 * Recursively wheels the myNumberSet.
	 * 
	 * @param howManyLeftToAdd
	 *            the number of Integers left to add to the Set
	 * @param lastUsedIndex
	 *            the last used index in the int[] arr
	 * @param result
	 *            the resulting Integer Set
	 * @return the final resulting Integer Set
	 */
	private List<Set<Integer>> getCombos(final int howManyLeftToAdd,
			final int lastUsedIndex, final Set<Integer> result) {
		final List<Set<Integer>> combos = new ArrayList<Set<Integer>>();
		if (howManyLeftToAdd == 1) {
			for (int j = lastUsedIndex; j < myNumbersArray.length; j++) {
				result.add(myNumbersArray[j]);
				combos.add(new TreeSet<Integer>(result));
				result.remove(myNumbersArray[j]);
			}
		} else {
			for (int j = lastUsedIndex; j < (myNumbersArray.length
					- howManyLeftToAdd + 1); j++) {
				if (myRepititionFlag) {
					result.add(myNumbersArray[j]);
					combos.addAll(getCombos(howManyLeftToAdd - 1, j,
							new HashSet<Integer>(result)));
					result.remove(myNumbersArray[j]);
				} else {
					result.add(myNumbersArray[j]);
					combos.addAll(getCombos(howManyLeftToAdd - 1, j + 1,
							new HashSet<Integer>(result)));
					result.remove(myNumbersArray[j]);
				}
			}
		}
		return combos;
	}

	/**
	 * Based on the settings on whether or not to wheel the output, create the
	 * final output string using helper functions.
	 * 
	 * @param wheelFlag
	 *            whether or not to wheel
	 * @param k
	 *            the number of positions to wheel
	 * @return the final output
	 */
	public String getOutput(final boolean wheelFlag, final int k) {
		final String result;
		if (wheelFlag) {
			result = getComboOutput(k);
		} else {
			result = toString();
		}
		return result;
	}

	/**
	 * Outputs a String of all combinations. Any bonus balls are also taken into
	 * account and are looped onto the end.
	 * 
	 * @param k
	 *            the number of numbers in the game
	 * @return a String of all combinations
	 */
	private String getComboOutput(final int k) {
		final List<String> result = new ArrayList<String>();
		final StringBuilder out = new StringBuilder();
		try {
			final List<Set<Integer>> output = getCombos(k);
			final Iterator<Set<Integer>> setItr = output.iterator();
			while (setItr.hasNext()) {
				final StringBuilder builder = new StringBuilder();
				final Iterator<Integer> numItr = setItr.next().iterator();
				while (numItr.hasNext()) {
					builder.append(numItr.next());
					builder.append(SPACE);
				}
				result.add(builder.toString());
			}
			if (myBonusBallFlag) {
				if (myBonusBallArray.length == 1) {
					addBonusBallToList(result, myBonusBallArray[0]);
				} else {
					final List<String> originalList = new ArrayList<String>(
							result);
					addBonusBallToList(result, myBonusBallArray[0]);
					for (int i = 1; i < myBonusBallArray.length; i++) {
						final List<String> listWithThisBall = new ArrayList<String>(
								originalList);
						addBonusBallToList(listWithThisBall,
								myBonusBallArray[i]);
						result.addAll(listWithThisBall);
					}
				}
			}
			for (String line : result) {
				out.append(line);
				out.append(MS_NEWLINE);
			}
		} catch (final IllegalArgumentException e) {
			out.append(toString());
		}
		return out.toString();
	}

	/**
	 * Given a list of wheel numbers, append the given bonus ball number to each
	 * string with a pip char to denote it as a bonus ball
	 * 
	 * @param list
	 *            the wheeled results
	 * @param bonusBall
	 *            the bonusball to add
	 */
	private static void addBonusBallToList(final List<String> list,
			final int bonusBall) {
		for (int i = 0; i < list.size(); i++) {
			String element = list.get(i);
			element = element + " | " + bonusBall;
			list.set(i, element);
		}
	}

	/**
	 * A simple output of the myNumberSet and bonus balls, if any.
	 * 
	 * @return a String of the myNumberSet and bonus balls
	 */
	@Override
	public String toString() {
		final StringBuilder out = new StringBuilder();
		out.append("Numbers:    ");
		out.append(MS_NEWLINE);
		out.append(sixNumbersPerLine(myNumbersArray)); // used to print set
		if (myBonusBallFlag) {
			out.append(MS_NEWLINE);
			out.append(MS_NEWLINE);
			out.append("BonusBalls: ");
			out.append(MS_NEWLINE);
			out.append(sixNumbersPerLine(myBonusBallArray));
		}
		return out.toString();
	}

	/**
	 * Given an array of numbers, create a String representation of that array
	 * with separating commas. After 6 numbers, add a new line to the String.
	 * 
	 * @param arr
	 *            the array of numbers to String
	 * @return the resulting string
	 */
	public static String sixNumbersPerLine(final int[] arr) {
		final StringBuilder builder = new StringBuilder();
		if (arr.length > 0) {
			int i = 0;
			while (i < arr.length - 1) {
				builder.append(arr[i]);
				builder.append(", ");
				i++;
				if (i % 6 == 0) {
					builder.append('\n');
				}
			}
			builder.append(arr[i]);
		} else {
			builder.append("none");
		}
		return builder.toString();
	}
}
