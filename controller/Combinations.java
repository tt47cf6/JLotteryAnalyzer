package controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import util.ArrayUtils;

/**
 * This class recursively generates a wheeling of a given size of given myNumberSet.
 * 
 * @author Robert Ogden
 * @version 2.1
 */
public final class Combinations {
    /** Space character for inbetween numbers on same line. */
    private static final char SPACE = ' ';
    /** Newline specific for Windows machines. Should work on macs. */
    private static final String MS_NEWLINE = "\r\n";
    /** The numbers to wheel. */
    private final int[] myNumbersArray;
    /** Bonus balls to wheel. */
    private int[] myBonusBallArray;
    /** If repetition of numbers is allowed. */
    private boolean myRepititionFlag;
    /** If bonus balls need to be added. */
    private boolean myBonusBallFlag;

    /**
     * This constructor takes in an Integer Set and creates an int[] using it
     * for direct use in this. Sets repetition to its default value, false.
     * 
     * @param theNumbers an Integer Set to create an int[] with
     */
    public Combinations(final Set<Integer> theNumbers) {
        myRepititionFlag = false;
        myBonusBallFlag = false;
        // convert set to array
        myNumbersArray = new int[theNumbers.size()];
        final Iterator<Integer> itr = theNumbers.iterator();
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
     * @param theNumbers the numbers to wheel
     * @param theBonusBalls the bonus balls to add to the end
     */
    public Combinations(final Set<Integer> theNumbers, 
                        final Set<Integer> theBonusBalls) {
        this(theNumbers);
        myBonusBallArray = new int[theBonusBalls.size()];
        myBonusBallFlag = true;
        final Iterator<Integer> itrB = theBonusBalls.iterator();
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
     * @param theAllow the value to set to myRepititionFlag
     */
    public void setRepitition(final boolean theAllow) {
        myRepititionFlag = theAllow;
    }

    /**
     * A helper method for the recursive method that creates the wheeling.
     * Throws an IllegalStateException if the size is less than the size of the
     * int[] myNumberSet.
     * 
     * @param theK the number of myNumberSet in the game
     * @return the wheeling of myNumberSet
     */
    private List<Set<Integer>> getCombos(final int theK) {
        if (theK > myNumbersArray.length || theK <= 0) {
            throw new IllegalArgumentException();
        }
        return getCombos(theK, 0, new HashSet<Integer>());
    }

    /**
     * Recursively wheels the myNumberSet.
     * 
     * @param theNumberLeftToAdd the number of Integers left to add to the Set
     * @param theLastUsedIndex the last used index in the int[] arr
     * @param theSet the resulting Integer Set
     * @return the final resulting Integer Set
     */
    private List<Set<Integer>> getCombos(final int theNumberLeftToAdd, 
                               final int theLastUsedIndex, final Set<Integer> theSet) {
        final List<Set<Integer>> combos = new ArrayList<Set<Integer>>();
        if (theNumberLeftToAdd == 1) {
            for (int j = theLastUsedIndex; j < myNumbersArray.length; j++) {
                theSet.add(myNumbersArray[j]);
                combos.add(new TreeSet<Integer>(theSet));
                theSet.remove(myNumbersArray[j]);
            }
        } else {
            for (int j = theLastUsedIndex; 
                            j < (myNumbersArray.length - theNumberLeftToAdd + 1); j++) {
                if (myRepititionFlag) {
                    theSet.add(myNumbersArray[j]);
                    combos.addAll(getCombos(theNumberLeftToAdd - 1, j, 
                                            new HashSet<Integer>(theSet)));
                    theSet.remove(myNumbersArray[j]);
                } else {
                    theSet.add(myNumbersArray[j]);
                    combos.addAll(getCombos(theNumberLeftToAdd - 1, j + 1, 
                                            new HashSet<Integer>(theSet)));
                    theSet.remove(myNumbersArray[j]);
                }
            }
        }
        return combos;
    }
    
    public String getOutput(final boolean theComboFlag, final int theK) {
        final String result;
        if (theComboFlag) {
            result = getComboOutput(theK);
        } else {
            result = toString();
        }
        return result;
    }

    /**
     * Outputs a String of all combinations. Any bonus balls are also taken into
     * account and are looped onto the end. 
     * 
     * @param theK the number of numbers in the game
     * @return a String of all combinations
     */
    private String getComboOutput(final int theK) {
        final List<String> result = new ArrayList<String>();
        final StringBuilder out = new StringBuilder(750);
        try {
            final List<Set<Integer>> output = getCombos(theK);
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
                    final List<String> originalList = new ArrayList<String>(result);
                    addBonusBallToList(result, myBonusBallArray[0]);
                    for (int i = 1; i < myBonusBallArray.length; i++) {
                        final List<String> listWithThisBall = new ArrayList<String>(originalList);
                        addBonusBallToList(listWithThisBall, myBonusBallArray[i]);
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
    
    private static void addBonusBallToList(final List<String> theList, final int theBonusBall) {
        for (int i = 0; i < theList.size(); i++) {
            String element = theList.get(i);
            element = element + " | " + theBonusBall;
            theList.set(i, element);
        }
    }

    /**
     * A simple output of the myNumberSet and bonus balls, if any.
     * 
     * @return a String of the myNumberSet and bonus balls
     */
    @Override
    public String toString() {
        final StringBuilder out = new StringBuilder(50);
        out.append("Numbers:    ");
        out.append(MS_NEWLINE);
        out.append(ArrayUtils.sixNumbersPerLine(myNumbersArray)); // used to print set
        if (myBonusBallFlag) {
            out.append(MS_NEWLINE);
            out.append(MS_NEWLINE);
            out.append("BonusBalls: ");
            out.append(MS_NEWLINE);
            out.append(ArrayUtils.sixNumbersPerLine(myBonusBallArray));
        }
        return out.toString();
    }
}
