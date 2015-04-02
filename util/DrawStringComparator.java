
package util;

import java.util.Comparator;

public final class DrawStringComparator implements Comparator<String> {

    private static final int DATE_LENGTH = 11;

    @Override
    public int compare(final String theFirst, final String theSecond) {
        final Date firstDate = getDate(theFirst.substring(0, DATE_LENGTH));
        final Date secondDate = getDate(theSecond.substring(0, DATE_LENGTH));
        return firstDate.compareTo(secondDate);
    }

    private Date getDate(final String in) {
        final int month = getMonth(in.substring(0, in.indexOf(' ')));
        final int day = Integer.parseInt(in.substring(in.indexOf(' ') + 1, in.lastIndexOf(' ')));
        final int year = Integer.parseInt(in.substring(in.lastIndexOf(' ') + 1));
        return new Date(month, day, year);
    }
    
    private static int getMonth(final String theMonth) {
        switch (theMonth) {
            case ("JAN"):
                return 1;
            case ("FEB"):
                return 2;
            case ("MAR"):
                return 3;
            case ("APR"):
                return 4;
            case ("MAY"):
                return 5;
            case ("JUN"):
                return 6;
            case ("JUL"):
                return 7;
            case ("AUG"):
                return 8;
            case ("SEP"):
                return 9;
            case ("OCT"):
                return 10;
            case ("NOV"):
                return 11;
            case ("DEC"):
                return 12;
        }
        throw new IllegalStateException("Invlaid Month: " + theMonth);
    }

    private class Date {

        private final int myMonth;

        private final int myDay;

        private final int myYear;

        public Date(final int theMonth, final int theDay, final int theYear) {
            myMonth = theMonth;
            myDay = theDay;
            myYear = theYear;
        }

        public int compareTo(final Date other) {
            if (myYear == other.myYear) {
                if (myMonth == other.myMonth) {
                    return other.myDay - myDay;
                }
                return other.myMonth - myMonth;
            }
            return other.myYear - myYear;
        }

    }

}
