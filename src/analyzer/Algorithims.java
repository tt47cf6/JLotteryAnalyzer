/*
 * Algorithms!
 */

package analyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import lotto.AbstractBonusBall;
import lotto.Lottery;
import util.ArrayUtils;
import util.ColdComparator;
import util.HotComparator;

/**
 * This is a module class, containing only all the methods needed to run the
 * various algorithms of the program.
 * 
 * @author Robert Ogden
 * @version 2.0
 */
public final class Algorithims {
    /**
     * private constructor to prevent making an Algorithms object.
     */
    private Algorithims() {

    }
    
    public static String getRawData(final Lottery theGame, 
                    final GetDraws theDraws) throws FileNotFoundException {
        final Map<Integer, Percentage> count = getNumberCount(theDraws);
        final Map<Percentage, Set<Integer>> hotNumbers = reverseMap(count, new HotComparator());
        final StringBuilder builder = new StringBuilder();
        for (Percentage key : hotNumbers.keySet()) {
            if (key.toString().length() < 18) {
                builder.append(key.toString());
            } else {
                builder.append(key.toString().substring(0, 18));
            }
            builder.append('\n');
            builder.append(hotNumbers.get(key));
            builder.append('\n');
            builder.append('\n');
        }
        return builder.toString();
    }

    public static Set<Integer> getHotNumbers(final Lottery theGame, final GetDraws theDraws)
                    throws FileNotFoundException {
        final Map<Integer, Percentage> count = getNumberCount(theDraws);
        final Map<Percentage, Set<Integer>> hotNumbers = reverseMap(count, new HotComparator());
        return getFinalResult(hotNumbers, theGame.getNumberOfBalls(), theGame.getRange());
    }
    
    private static Map<Integer, Percentage> getNumberCount(final GetDraws theDraws) 
                    throws FileNotFoundException {
        final Map<Integer, Percentage> count = new HashMap<Integer, Percentage>();
        final List<Draw> draws = theDraws.getDraws();
        for (final Draw draw : draws) {
            final int[] numbers = draw.numbers();
            // ensure the map has all the numbers
            final int range = draw.numberRange();
            for (int i = 1; i <= range; i++) {
                if (!count.containsKey(i)) {
                    count.put(i, Percentage.ZERO);
                }
            }
            // increase and decrease
            for (int i = 1; i <= range; i++) {
                if (ArrayUtils.arrayContains(numbers, i)) {
                    count.put(i, count.get(i).addOccurance());
                } else {
                    count.put(i, count.get(i).addNonOccurance());
                }
            }
        }
        return count;
    }
    
    private static Map<Integer, Percentage> getBonusBallCount(final GetDraws theDraws) 
                    throws FileNotFoundException {
        final Map<Integer, Percentage> count = new HashMap<Integer, Percentage>();
        final List<Draw> draws = theDraws.getDraws();
        for (final Draw draw : draws) {
            final int bonusBall = draw.bonusBall();
            // ensure the map has all the numbers
            final int range = draw.bonusBallRange();
            for (int i = 1; i <= range; i++) {
                if (!count.containsKey(i)) {
                    count.put(i, Percentage.ZERO);
                }
            }
            // increase and decrease
            for (int i = 1; i <= range; i++) {
                if (bonusBall == i) {
                    count.put(i, count.get(i).addOccurance());
                } else {
                    count.put(i, count.get(i).addNonOccurance());
                }
            }
        }
        return count;
    }

    private static Map<Percentage, Set<Integer>> reverseMap(final Map<Integer, 
                        Percentage> theOriginalMap, final Comparator<Percentage> theCompare) {
        final Map<Percentage, Set<Integer>> result = new TreeMap<Percentage, Set<Integer>>(theCompare);
        final Set<Percentage> originalValueSet = new HashSet<Percentage>(theOriginalMap.values());
        // go through a set of the original values
        for (final Percentage originalValue : originalValueSet) {
            final Set<Integer> newValueSet = new HashSet<Integer>();
            // go through a set of the original keys
            for (final int oldKey : theOriginalMap.keySet()) {
                // if the original value equals the current value in the loop,
                // keep it
                if (theOriginalMap.get(oldKey).equals(originalValue)) {
                    newValueSet.add(oldKey);
                }
            }
            // add to result
            result.put(originalValue, newValueSet);
        }
        return result;
    }

    /**
     * This method gets the least most frequently occurring numbers.
     * 
     * @param theGame the game to be analyzed
     * @param theDraws the GetDraws object to get draws from
     * @return an Integer Set of 'cold' numbers
     * @throws FileNotFoundException exception from GetDraws.getDraws()
     */
    public static Set<Integer> getColdNumbers(final Lottery theGame, final GetDraws theDraws)
                    throws FileNotFoundException {
        final Map<Integer, Percentage> count = getNumberCount(theDraws);
        final Map<Percentage, Set<Integer>> coldNumbers = reverseMap(count, new ColdComparator());
        return getFinalResult(coldNumbers, theGame.getNumberOfBalls(), theGame.getRange());
    }

    /**
     * This method looks back several draws where, in a perfectly random system
     * one would expect those numbers to come up again in the next draw. This is
     * determined by dividing the range by the number of balls, and going back
     * and fetching numbers from that number of draws ago. This method does NOT
     * use the GetDraws object.
     * 
     * @param theGame the game to fetch for
     * @return an Integer Set
     * @throws FileNotFoundException database file doesn't exist
     */
    public static Set<Integer> getPeriodicNumbers(final Lottery theGame)
                    throws FileNotFoundException {
        final Scanner in = new Scanner(new File(theGame.dataFile()));
        final int weekFreq = theGame.getRange() / theGame.getNumberOfBalls();
        final Set<Integer> result = new HashSet<Integer>();
        for (int i = 0; i < weekFreq - 1; i++) {
            in.nextLine();
        }
        // consume date
        in.next();
        in.next();
        in.next();
        for (int i = 0; i < theGame.getNumberOfBalls(); i++) {
            result.add(in.nextInt());
        }
        in.close();
        return result;
    }

    /**
     * Gets the most frequently occurring bonus balls.
     * 
     * @param theGame the AbstractBonusBall game to analyze
     * @param theDraws the GetDraws object to get input from
     * @return an Integer Set, likely to be small in size
     * @throws FileNotFoundException exception from GetDraws.getDraws()
     */
    public static Set<Integer> getHotBonusBalls(final AbstractBonusBall theGame,
                                                final GetDraws theDraws)
                    throws FileNotFoundException {
        final Map<Integer, Percentage> count = getBonusBallCount(theDraws);
        final Map<Percentage, Set<Integer>> hotBonusBalls = reverseMap(count, new HotComparator());
        return getFinalResult(hotBonusBalls, theGame.getNumberOfBonusBalls(),
                              theGame.getBonusBallRange());
    }

    /**
     * Gets the least frequently occurring bonus balls.
     * 
     * @param theGame the AbstractBonusBall game to analyze
     * @param theDraws the GetDraws object to get input from
     * @return an Integer Set
     * @throws FileNotFoundException exception from GetDraws.getDraws()
     */
    public static Set<Integer> getColdBonusBalls(final AbstractBonusBall theGame,
                                                 final GetDraws theDraws)
                    throws FileNotFoundException {
        final Map<Integer, Percentage> count = getBonusBallCount(theDraws);
        final Map<Percentage, Set<Integer>> coldBonusBalls = reverseMap(count, new ColdComparator());
        return getFinalResult(coldBonusBalls, theGame.getNumberOfBonusBalls(),
                              theGame.getBonusBallRange());
    }

    /**
     * A helper method for most above methods. This method takes in an Integer
     * -> Set<Integer> mapping, and outputs a single Tree Set according to the
     * number of balls given and removes numbers above the given max
     * 
     * @param theBalls a mapping of frequency -> balls
     * @param theSize the minimum size of the resulting Set
     * @param theMax exclude numbers above this
     * @return a TreeSet of resulting numbers
     */
    private static Set<Integer> getFinalResult(final Map<Percentage, Set<Integer>> theBalls,
                                               final int theSize, final int theMax) {
        final Set<Integer> result = new TreeSet<Integer>();
        for (final Percentage key : theBalls.keySet()) {
            if (result.size() < theSize) {
                result.addAll(theBalls.get(key));
                final Iterator<Integer> itr = result.iterator();
                while (itr.hasNext()) {
                    final int next = itr.next();
                    if (next > theMax || next <= 0) {
                        itr.remove();
                    } 
                }
            }
            
        }
        return result;
    }

    /**
     * Returns the bonus balls from one periodic draw ago.
     * 
     * @param theGame the game to look up
     * @return the periodic bonus ball
     * @throws FileNotFoundException if the database file is not found
     */
    public static Set<Integer> getPeriodicBonusBalls(final AbstractBonusBall theGame)
                    throws FileNotFoundException {
        final Scanner in = new Scanner(new File(theGame.dataFile()));
        final int weekFreq = theGame.getRange() / theGame.getNumberOfBalls();
        final Set<Integer> result = new HashSet<Integer>();
        for (int i = 0; i < weekFreq - 1; i++) {
            in.nextLine();
        }
        // consume date
        in.next();
        in.next();
        in.next();
        for (int i = 0; i < theGame.getNumberOfBalls(); i++) {
            in.nextInt();
        }
        result.add(in.nextInt());
        in.close();
        return result;
    }
}
