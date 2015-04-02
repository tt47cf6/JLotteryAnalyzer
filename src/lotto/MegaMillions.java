
package lotto;


/**
 * This class holds the data for the MegaMillions game. It implements the
 * Lottery interface and also holds MegaMillion's database file as well as the
 * game information.
 * 
 * @author Robert Ogden
 * @version 1.0
 */
public final class MegaMillions extends AbstractBonusBall implements Lottery {
    /** The upper limit of the number balls, inclusive. */
    public static final int RANGE = 75;
    /** The number of balls in each drawing. */
    public static final int NUMBER_OF_BALLS = 5;
    /** The upper limit of the bonus balls, inclusive. */
    public static final int BONUS_BALL_RANGE = 15;
    /** The number of bonus balls. Normally 1. */
    public static final int NUMBER_OF_BONUS_BALLS = 1;
    /** The database file for this game. In the current directory. */
    public static final String DATA_FILE = "mm.txt";
    /** The web url for updating this game's database. */
    public static final String UPDATE_URL =
                    "http://www.walottery.com/WinningNumbers/Search.aspx?game=39&year=";

    /**
     * Create a new instance of this.
     */
    public MegaMillions() {
        super(RANGE, NUMBER_OF_BALLS, BONUS_BALL_RANGE, NUMBER_OF_BONUS_BALLS, DATA_FILE);
    }

    @Override
    public String updateURL() {
        return UPDATE_URL;
    }
    
    public String toString() {
        return "Mega Millions";
    }
}
