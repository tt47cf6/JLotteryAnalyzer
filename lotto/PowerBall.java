package lotto;


/**
 * This class contains all the information for the PowerBall game.
 * 
 * @author Robert Ogden
 * @version 2.0
 */
public final class PowerBall extends AbstractBonusBall implements Lottery {
    /** The upper limit of the number ball range, inclusive. */
    public static final int RANGE = 59;
    /** The number of number balls. */
    public static final int NUMBER_OF_BALLS = 5;
    /** The upper limit of the bonus ball range, inclusive. */
    public static final int BONUS_BALL_RANGE = 35;
    /** The number of bonus balls. */
    public static final int NUMBER_OF_BONUS_BALLS = 1;
    /** The database file for this game. */
    public static final String DATA_FILE = "pb.txt";
    /** The web address used to update this game's database. */
    public static final String UPDATE_URL =
                    "http://www.walottery.com/WinningNumbers/Search.aspx?game=44&year=";

    /** 
     * Creates a new instance of this.
     */
    public PowerBall() {
        super(RANGE, NUMBER_OF_BALLS, BONUS_BALL_RANGE, NUMBER_OF_BONUS_BALLS, DATA_FILE);
    }
    
    @Override
    public String updateURL() {
        return UPDATE_URL;
    }
    
    public String toString() {
        return "PowerBall";
    }
}
