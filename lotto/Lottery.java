
package lotto;

import java.io.FileNotFoundException;

/**
 * This interface establishes the needed getters for all Lottery classes. 
 * 
 * @author Robert
 */
public interface Lottery {
    
	/**
	 * A constant identifier for Washington Lottery.
	 */
    public static final String WA_LOTTO = "WA Lotto";
    
    /**
     * A constant identifier for Mega Millions.
     */
    public static final String MEGA_MILLIONS = "Mega Millions";
    
    /**
     * A common identifier for PowerBall.
     */
    public static final String POWERBALL = "PowerBall";
    
    /**
     * The minimum year that all games can be found online for updating.
     */
    public static final int MIN_COMMON_YEAR = 2009;
    
    /**
     * Returns the range of the number balls, inclusive.
     * 
     * @return the range of the number balls
     */
    int getRange();

    /**
     * Returns the number of number balls.
     * 
     * @return the number of balls
     */
    int getNumberOfBalls();

    /**
     * Returns true if this game has a bonus ball.
     * 
     * @return if this game has a bonus ball
     */
    boolean hasBonusBall();

    /**
     * Returns the filename of this game's database.
     * 
     * @return the database file name.
     */
    String dataFile();
    
    /**
     * Get the base URL for updating this game.
     * 
     * @return the base update URL
     */
    String updateURL();

    /**
     * Returns the number of draws contained in the database. Used for checking
     * user input for the number of draws to count.
     * 
     * @return the number of draws in the database
     * @throws FileNotFoundException if the database file is missing
     */
    int dataFileLength() throws FileNotFoundException;
}
