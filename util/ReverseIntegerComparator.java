
package util;

import java.util.Comparator;

/**
 * This class is a comparator for integers that reverses their ordering. 
 * 
 * @author Robert
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
