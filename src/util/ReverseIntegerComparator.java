
package util;

import java.util.Comparator;

/**
 * This class is a comparator for integers that reverses their ordering. It is
 * used by the GetDraws class to put most frequent numbers first in the TreeSet.
 * 
 * @author Robert Ogden
 * @version 2.0
 */
public final class ReverseIntegerComparator implements Comparator<Integer> {

    /**
     * Compares integers in reverse.
     * 
     * @param int1 the first integer
     * @param int2 the second integer
     * @return how the integers relate, reversely
     */
    public int compare(final Integer int1, final Integer int2) {
        return int2 - int1;
    }
}
