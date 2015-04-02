
package lotto;


/**
 * An abstract class representing a Lottery game with a bonus ball.
 * 
 * @author Robert Ogden
 * @version 2.0
 */
public abstract class AbstractBonusBall extends AbstractLottery implements Lottery {
    /** Constant for the presence of a bonus ball in games that extend this. */
    private static final boolean HAS_BONUS_BALL = true;
    /** The upper limit of the bonus ball range, inclusive. */
    private final int myBonusBallRange;
    /** The number of bonus balls. */
    private final int myNumberOfBonusBalls;
    
    /**
     * A protected constructor that sets the fields according to the passed values
     * from the child class.
     * 
     * @param theRange the ball number range
     * @param theNumberOfBalls the number of number balls
     * @param theBonusBallRange the bonus ball range
     * @param theNumberOfBonusBalls the number of bonus balls
     * @param theFile the database file for this game
     */
    protected AbstractBonusBall(final int theRange, final int theNumberOfBalls, 
                                final int theBonusBallRange, final int theNumberOfBonusBalls,
                                final String theFile) {
        super(theRange, theNumberOfBalls, theFile);
        myBonusBallRange = theBonusBallRange;
        myNumberOfBonusBalls = theNumberOfBonusBalls;
    }
    
    /**
     * Given that this game has a bonus ball, always return true.
     * 
     * @return always true for classes that extend this
     */
    @Override
    public boolean hasBonusBall() {
        return HAS_BONUS_BALL;
    }

    /**
     * Returns the top value of the range that bonus balls can have. 
     * 
     * @return the top value of a bonus ball
     */
    public int getBonusBallRange() {
        return myBonusBallRange;
    }

    /**
     * Returns the number of bonus balls in this game.
     * 
     * @return the number of bonus balls
     */
    public int getNumberOfBonusBalls() {
        return myNumberOfBonusBalls;
    }
}
