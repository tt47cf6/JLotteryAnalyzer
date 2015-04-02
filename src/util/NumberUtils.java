
package util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public final class NumberUtils {

    private static final int PARSE_DEFAULT = 0;

    private NumberUtils() {
        // do nothing
    }
    
    public static long parseLong(final String theText) {
        long result = PARSE_DEFAULT;
        try {
            result = Math.max(PARSE_DEFAULT, Long.parseLong(theText));
        } catch (NumberFormatException e) {
            result = PARSE_DEFAULT;
        }
        return result;
    }

    public static int parseInteger(final String theText) {
        int result = PARSE_DEFAULT;
        try {
            result = Math.max(PARSE_DEFAULT, Integer.parseInt(theText));
        } catch (NumberFormatException e) {
            result = PARSE_DEFAULT;
        }
        return result;
    }
    
    public static long parseCurrency(final String theText) {
        String temp = theText;
        if (temp.startsWith("$")) {
            temp = temp.substring(1);
        }
        while (temp.contains(",")) {
            final int index = temp.indexOf(',');
            temp = temp.substring(0, index) + temp.substring(index + 1);
        }
        return Long.parseLong(temp);
    }
    
    public static int[] parseNumbers(final String theText) {
        final int[] result = new int[(theText.length() + 1) / 3];
        final Scanner scan = new Scanner(theText);
        int i = 0;
        while (scan.hasNextInt()) {
            result[i] = scan.nextInt();
            i++;
        }
        return result;
    }

    public static List<String> delimit(final String delimiter, final String content) {
        final List<String> result = new ArrayList<String>();
        final StringBuilder builder = new StringBuilder(content);
        int indexOfDelimiter = builder.indexOf(delimiter);
        while (indexOfDelimiter != -1) {
            result.add(builder.substring(0, indexOfDelimiter));
            builder.delete(0, indexOfDelimiter + delimiter.length());
            indexOfDelimiter = builder.indexOf(delimiter);
        }
        result.add(builder.toString());
        return result;
    }

    public static List<String> delimit(final char delimiter, final String content) {
        return delimit("" + delimiter, content);
    }
    
    public static Set<Integer> parseCustomNumbers(final String theInput) {
        final List<String> asStrings = delimit(' ', theInput);
        final Set<Integer> result = new HashSet<Integer>();
        for (String number : asStrings) {
            result.add(parseInteger(number));
        }
        result.remove(PARSE_DEFAULT);
        return result;
    }
    
    public static int[] listToArray(final List<String> theList) {
        final int[] result = new int[theList.size()];
        int i = 0;
        for (String element : theList) {
            final int num = Integer.parseInt(element);
            result[i] = num;
            i++;
        }
        return result;
    }
    
    public static String setAsString(final Set<Integer> theSet) {
        String result = "";
        for (int num : theSet) {
            result += num + " ";
        }
        return result;
    }
}
