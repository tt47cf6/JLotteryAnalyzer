/**
 * 
 */
package util;

import java.util.Comparator;

import analyzer.Percentage;

/**
 * This comparator works to compare two percentages, each that are comparable,
 *  in such a way that bigger percentages will be given first in an ordered list, 
 *  and lesser will be later in the list.
 *  
 * @author Robert
 */
public final class HotComparator implements Comparator<Percentage> {

	/**
	 * Compares the two Percentages such that an ordered list will have 
	 * bigger values first.
	 * 
	 * @param first the first value to compare
	 * @param second the second value to compare
	 */
    @Override
    public int compare(final Percentage first, final Percentage second) {
        return second.compareTo(first);
    }

}
