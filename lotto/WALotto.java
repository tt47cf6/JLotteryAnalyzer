package lotto;

/**
 * This class contains all the information about the Washington State Lottery
 * game.
 * 
 * @author Robert
 */
public final class WALotto extends AbstractLottery implements Lottery {
	/**
	 * The web address used to update this game's database.
	 */
	public static final String UPDATE_URL = "http://www.walottery.com/WinningNumbers/Search.aspx?game=37&year=";
	/**
	 * The upper limit of the number range, inclusive.
	 */
	private static final int RANGE = 49;
	/**
	 * The number of balls in this game.
	 */
	private static final int NUMBER_OF_BALLS = 6;
	/**
	 * The filename for this game's database.
	 */
	private static final String DATA_FILE = "wa.txt";

	/**
	 * Create a new instance of this.
	 */
	public WALotto() {
		super(RANGE, NUMBER_OF_BALLS, DATA_FILE);
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasBonusBall() {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public String updateURL() {
		return UPDATE_URL;
	}

	/** {@inheritDoc} */
	public String toString() {
		return "WA Lottery";
	}
}
