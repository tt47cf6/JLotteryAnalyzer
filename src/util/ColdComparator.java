/**
 * 
 */
package util;

import java.util.Comparator;

import analyzer.Percentage;

/**
 * @author Robert
 *
 */
public final class ColdComparator implements Comparator<Percentage> {

    @Override
    public int compare(final Percentage theFirst, final Percentage theSecond) {
        return theFirst.compareTo(theSecond);
    }
}
