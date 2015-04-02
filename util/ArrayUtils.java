
package util;

public final class ArrayUtils {

    private static final char NEWLINE = '\n';
    private static final int NUMBERS_PER_LINE = 6;

    private ArrayUtils() {
        // do nothing
    }

    public static String sixNumbersPerLine(final int[] theArr) {
        final StringBuilder builder = new StringBuilder();
        if (theArr.length > 0) {
            int i = 0;
            while (i < theArr.length - 1) {
                builder.append(theArr[i]);
                builder.append(", ");
                i++;
                if (i % NUMBERS_PER_LINE == 0) {
                    builder.append(NEWLINE);
                }
            }
            builder.append(theArr[i]);
        } else {
            builder.append("none");
        }
        return builder.toString();
    }
    
    public static boolean arrayContains(final int[] theArr, final int theNum) {
        for (int num : theArr) {
            if (num == theNum) {
                return true;
            }
        }
        return false;
    }

}
