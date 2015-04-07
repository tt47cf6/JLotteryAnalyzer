package util;

import java.util.Comparator;

import analyzer.Draw;

/**
 * This class compares two dates that are represented as strings according to
 * the pattern in the database files. A simple inner Date class is used to avoid
 * the complexity and excessive functionality of the Java Date class.
 * 
 * @author Robert
 */
public final class DrawStringComparator implements Comparator<String> {

	/**
	 * Compares two lines of a draw file where the first three fields are the
	 * date of the object. The date is expected to be in the given format of the
	 * Draw toString() method. This comparator puts most recent dates first in a
	 * sorted list.
	 * 
	 * @param first
	 *            the first date to compare
	 * @param second
	 *            the second date to compare
	 */
	@Override
	public int compare(final String first, final String second) {
		final Date firstDate = getDate(first);
		final Date secondDate = getDate(second);
		return firstDate.compareTo(secondDate);
	}

	// TODO clean up Draw toString() to use String.format()

	/**
	 * Parses out a date object given the input string.
	 * 
	 * @param in
	 *            input string of the draw in the expected pattern
	 * @return a date object to compare
	 */
	private Date getDate(final String in) {
		final String[] split = in.split(Draw.DELIMITER);
		final int month = getMonth(split[0]);
		final int day = Integer.parseInt(split[1]);
		final int year = Integer.parseInt(split[2]);
		return new Date(month, day, year);
	}

	/**
	 * A helper method that returns the integer of the String-represented given
	 * month.
	 * 
	 * @param month
	 *            the month to represent as an int
	 * @return the int represented by the given string
	 */
	private static int getMonth(final String month) {
		switch (month) {
		case ("JAN"):
			return 1;
		case ("FEB"):
			return 2;
		case ("MAR"):
			return 3;
		case ("APR"):
			return 4;
		case ("MAY"):
			return 5;
		case ("JUN"):
			return 6;
		case ("JUL"):
			return 7;
		case ("AUG"):
			return 8;
		case ("SEP"):
			return 9;
		case ("OCT"):
			return 10;
		case ("NOV"):
			return 11;
		case ("DEC"):
			return 12;
		}
		throw new IllegalStateException("Invlaid Month: " + month);
	}

	/**
	 * A simple Date object to compare two dates.
	 * 
	 * @author Robert
	 */
	private class Date implements Comparable<Date> {

		/**
		 * The month. Range: [1, 12]
		 */
		private final int myMonth;

		/**
		 * The date. Range: [1, 31]
		 */
		private final int myDay;

		/**
		 * The year.
		 */
		private final int myYear;

		/**
		 * A simple constructor to set the fields to their given values.
		 * 
		 * @param month
		 *            month
		 * @param day
		 *            day
		 * @param year
		 *            year
		 */
		public Date(final int month, final int day, final int year) {
			myMonth = month;
			myDay = day;
			myYear = year;
		}

		/**
		 * Compare another Date with this. In the sorted list, more recent dates
		 * will be placed first.
		 * 
		 * @param other
		 *            the other Date to compare to
		 */
		@Override
		public int compareTo(final Date other) {
			if (myYear == other.myYear) {
				if (myMonth == other.myMonth) {
					return other.myDay - myDay;
				}
				return other.myMonth - myMonth;
			}
			return other.myYear - myYear;
		}

	}

}
