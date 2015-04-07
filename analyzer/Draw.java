package analyzer;

/**
 * A simple object that represents a single drawing, with bonus ball as
 * applicable.
 * 
 * @author Robert Ogden
 * @version 2.0
 */
public final class Draw {

	/**
	 * The delimiter of fields in the database.
	 */
	public static final String DELIMITER = "\t";
	/**
	 * A string of the date of the draw.
	 */
	private final String myDate;
	/**
	 * The regular number balls that were drawn.
	 */
	private final int[] myNumbers;
	/**
	 * The bonus ball that was drawn, if any.
	 */
	private final int myBonusBall;
	/**
	 * If this was a jackpot winner.
	 */
	private final boolean myJackpotStatus;
	/**
	 * The total payout of this.
	 */
	private final long myPayout;
	/**
	 * The range of number balls applicable in this drawing.
	 */
	private final int myNumberRange;
	/**
	 * The range of bonus balls applicable in this drawing.
	 */
	private final int myBonusBallRange;

	/**
	 * Constructor for a draw with a bonus ball.
	 * 
	 * @param theDate
	 *            draw date
	 * @param theNumbers
	 *            numbers drawn
	 * @param theBonusBall
	 *            the bonus ball drawn
	 * @param theJackpotStatus
	 *            if this draw was a jackpot winner
	 * @param thePayout
	 *            the payout
	 * @param theNumberRange
	 *            the number range applicable on this draw
	 * @param theBonusBallRange
	 *            the bonus ball range applicable on this draw
	 */
	public Draw(final String theDate, final int[] theNumbers,
			final int theBonusBall, final boolean theJackpotStatus,
			final long thePayout, final int theNumberRange,
			final int theBonusBallRange) {
		myDate = theDate;
		myNumbers = theNumbers.clone();
		myBonusBall = theBonusBall;
		myJackpotStatus = theJackpotStatus;
		myPayout = thePayout;
		myNumberRange = theNumberRange;
		myBonusBallRange = theBonusBallRange;
	}

	/**
	 * A constructor if this draw does not have a bonus ball. Sets fields
	 * pertaining to bonus balls to 0
	 * 
	 * @param theDate
	 *            the draw date
	 * @param theNumbers
	 *            the numbers drawn
	 * @param theJackpotStatus
	 *            if this draw was a jackpot winner
	 * @param thePayout
	 *            the total payout from this drawing
	 * @param theNumberRange
	 *            the range of applicable numbers
	 */
	public Draw(final String theDate, final int[] theNumbers,
			final boolean theJackpotStatus, final long thePayout,
			final int theNumberRange) {
		myDate = theDate;
		myNumbers = theNumbers.clone();
		myJackpotStatus = theJackpotStatus;
		myPayout = thePayout;
		myNumberRange = theNumberRange;
		myBonusBall = 0;
		myBonusBallRange = 0;
	}

	/**
	 * A constructor that makes a duplicate Draw object to the one passed.
	 * 
	 * @param theOther
	 *            the Draw object to copy
	 */
	public Draw(final Draw theOther) {
		myDate = theOther.myDate;
		myNumbers = theOther.myNumbers.clone();
		myBonusBall = theOther.myBonusBall;
		myJackpotStatus = theOther.myJackpotStatus;
		myPayout = theOther.myPayout;
		myNumberRange = theOther.myNumberRange;
		myBonusBallRange = theOther.myBonusBallRange;
	}

	/**
	 * A getter for the date field.
	 * 
	 * @return the draw date
	 */
	public String date() {
		return myDate;
	}

	/**
	 * A getter for the numbers field.
	 * 
	 * @return the numbers drawn
	 */
	public int[] numbers() {
		return myNumbers.clone();
	}

	/**
	 * A getter for the bonus ball field.
	 * 
	 * @return the bonus ball drawn
	 */
	public int bonusBall() {
		return myBonusBall;
	}

	/**
	 * A getter for the jackpotWinner field.
	 * 
	 * @return the jackpotWinner field
	 */
	public boolean jackpotWinner() {
		return myJackpotStatus;
	}

	/**
	 * A getter for the payout of this draw.
	 * 
	 * @return the payout of this draw
	 */
	public long payout() {
		return myPayout;
	}

	/**
	 * The applicable range of numbers for this draw.
	 * 
	 * @return the applicable range of numbers for this draw
	 */
	public int numberRange() {
		return myNumberRange;
	}

	/**
	 * The applicable range of bonus balls for this draw.
	 * 
	 * @return the range of bonus balls for this draw
	 */
	public int bonusBallRange() {
		return myBonusBallRange;
	}

	/**
	 * This method is used primarily for updating.
	 * 
	 * @return a string to save to an update file of all this' fields
	 */
	@Override
	public String toString() {
		final StringBuilder out = new StringBuilder(75);
		out.append(myDate);
		out.append(DELIMITER);
		for (final int num : myNumbers) {
			if (num < 10) {
				out.append('0');
			}
			out.append(num);
			out.append(DELIMITER);
		}
		out.append(DELIMITER);
		if (myBonusBall > 0) {
			out.append(myBonusBall);
			out.append(DELIMITER);
		}
		if (myJackpotStatus) {
			out.append("TRUE");
		} else {
			out.append("FALSE");
		}
		out.append(DELIMITER);
		out.append(DELIMITER);
		out.append(myPayout);
		out.append(DELIMITER);
		out.append(myNumberRange);
		if (myBonusBall > 0) {
			out.append(DELIMITER);
			out.append(myBonusBallRange);
		}
		return out.toString();
	}

	public static int parseInt(final String in) {
		String result = in;
		if (result.contains("$")) {
			result = result.substring(result.indexOf("$") + 1);
		}
		while (result.contains(",")) {
			int idx = result.indexOf(",");
			result = result.substring(0, idx) + result.substring(idx + 1);
		}
		System.out.println(result);
		return Integer.parseInt(result);
	}

	public static long parseLong(final String in) {
		String result = in;
		if (result.contains("$")) {
			result = result.substring(result.indexOf("$") + 1);
		}
		while (result.contains(",")) {
			int idx = result.indexOf(",");
			result = result.substring(0, idx) + result.substring(idx + 1);
		}
		return Long.parseLong(result);
	}

	public static String parseDate(final String in) {

		String result = in;
		result = result.substring(result.indexOf(" ") + 1);
		result = result.substring(0, result.indexOf(","))
				+ result.substring(result.indexOf(",") + 1);
		return result;
	}
}
