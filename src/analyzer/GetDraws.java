
package analyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import lotto.Lottery;

/**
 * Using the Lottery game field class, this class takes in everything from the
 * associated input file and returns a List of Draw objects.
 * 
 * @author Robert Ogden
 * @version 2.0
 */
public final class GetDraws {
    /**
     * The minimum number of draws to count. Anything below is rounded up
     */
    public static final int MIN_DRAWS_TO_COUNT = 15;
    /** A space character to put inbetween date numerals. */
    private static final char SPACE = ' ';
    /**
     * The expected String in the file for a jackpot winner, upper or lower
     * case.
     */
    private static final String IF_JACKPOT_WINNER = "TRUE";
    /**
     * The number of draws to count. 0 = all, minimum is set above
     */
    private int myNumberOfDrawsToCount;
    /**
     * If true, count only draws that were jackpot winners.
     */
    private final boolean myJackpotStatus;
    /**
     * Calculate only draws that paid out above this amount, default of 0.
     */
    private final long myPayout;
    /**
     * Which game to calculate for.
     */
    private final Lottery myGame;

    /**
     * Sole Constructor Sets this fields to passed arguments.
     * 
     * @param theGame which game to analyze
     * @param theDrawsToCount how many draws to count
     * @param theJackpotStatus jackpot winner
     * @param thePayout payout above
     */
    public GetDraws(final Lottery theGame, final int theDrawsToCount,
                    final boolean theJackpotStatus, final long thePayout) {
        myNumberOfDrawsToCount = theDrawsToCount;
        myJackpotStatus = theJackpotStatus;
        myPayout = thePayout;
        myGame = theGame;
    }

    /**
     * A helper method that routes to appropriate method based on whether the
     * game has a bonus ball.
     * 
     * @return a List of Draw objects of all draws matching the params of this
     * @throws FileNotFoundException if the database file is not found
     */
    public List<Draw> getDraws() throws FileNotFoundException {
        List<Draw> result;
        if (myGame.hasBonusBall()) {
            result = getDrawsWithBonusBall();
        } else {
            result = getDrawsNoBonusBall();
        }
        return result;
    }

    /**
     * Gets the draws for games without a bonus ball.
     * 
     * @return a List of Draw objects
     * @throws FileNotFoundException if the database file is not found
     */
    private List<Draw> getDrawsNoBonusBall() throws FileNotFoundException {
        final List<Draw> draws = new ArrayList<Draw>();
        final Scanner in = new Scanner(new File(myGame.dataFile()));
        // if n is greater than zero but less than the minimum, round up
        if (myNumberOfDrawsToCount > 0 && myNumberOfDrawsToCount < MIN_DRAWS_TO_COUNT) {
            myNumberOfDrawsToCount = MIN_DRAWS_TO_COUNT;
        } else if (myNumberOfDrawsToCount == 0 || myNumberOfDrawsToCount > myGame.dataFileLength()) {
            myNumberOfDrawsToCount = myGame.dataFileLength();
        }
        for (int i = 0; i < myNumberOfDrawsToCount; i++) {
            final String date = in.next() + SPACE + in.next() + SPACE + in.next();
            final int[] numbers = new int[myGame.getNumberOfBalls()];
            for (int j = 0; j < numbers.length; j++) {
                numbers[j] = in.nextInt();
            }
            final boolean winner = in.next().equalsIgnoreCase(IF_JACKPOT_WINNER);
            final long payout = Long.parseLong(in.next());
            final int range = in.nextInt();
            boolean addThisDraw = !myJackpotStatus || winner;
            addThisDraw = addThisDraw && payout >= myPayout;
            if (addThisDraw) {
                draws.add(new Draw(date, numbers, winner, payout, range));
            }
        }
        in.close();
        return draws;
    }

    /**
     * Gets a List of Draw objects for games with a bonus ball.
     * 
     * @return a List of Draw objects matching this params.
     * @throws FileNotFoundException if the database file is not found
     */
    private List<Draw> getDrawsWithBonusBall() throws FileNotFoundException {
        final List<Draw> draws = new ArrayList<Draw>();
        final Scanner in = new Scanner(new File(myGame.dataFile()));
        if (myNumberOfDrawsToCount > 0 && myNumberOfDrawsToCount < MIN_DRAWS_TO_COUNT) {
            myNumberOfDrawsToCount = MIN_DRAWS_TO_COUNT;
        } else if (myNumberOfDrawsToCount == 0 || myNumberOfDrawsToCount > myGame.dataFileLength()) {
            myNumberOfDrawsToCount = myGame.dataFileLength();
        }
            for (int i = 0; i < myNumberOfDrawsToCount; i++) {
                final String date = in.next() + SPACE + in.next() + SPACE + in.next();
                final int[] numbers = new int[myGame.getNumberOfBalls()];
                for (int j = 0; j < numbers.length; j++) {
                    numbers[j] = in.nextInt();
                }
                final int bonusBall = in.nextInt();
                final boolean winner = in.next().equalsIgnoreCase(IF_JACKPOT_WINNER);
                final long payout = Long.parseLong(in.next());
                final int numberRange = in.nextInt();
                final int bonusBallRange = in.nextInt();
                boolean addThisDraw = !myJackpotStatus || winner;
                addThisDraw = addThisDraw && payout >= myPayout;
                if (addThisDraw) {
                    draws.add(new Draw(date, numbers, bonusBall, winner, payout, numberRange, bonusBallRange));
                }
            }
        
        in.close();
        return draws;
    }
}
