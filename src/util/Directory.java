package util;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public final class Directory {

	public static Set<String> getFileNamesFrom(final String theDirectory) {
		final File directory = new File(theDirectory);
		if (!directory.isDirectory() || !directory.exists()) {
			throw new IllegalArgumentException(theDirectory
					+ "is not a vaild directory!");
		}
		return new HashSet<String>(Arrays.asList(directory.list()));
	}
	
	public static String removeFileExtension(final String theFile) {
		String result = theFile;
	    if (result.indexOf('.') != -1) {
	        final int indexOfExt = theFile.lastIndexOf('.');
	        result = theFile.substring(0, indexOfExt);
		}
		return result;
	}
	
	public static Set<String> removeFileExtension(final Set<String> theSet) {
		Iterator<String> itr = theSet.iterator();
		Set<String> result = new HashSet<String>();
		while (itr.hasNext()) {
			final String theFile = itr.next();
			result.add(removeFileExtension(theFile));
		}
		return result;
	}
	
	public static String textFileAsString(final Scanner in) {
        final StringBuilder builder = new StringBuilder();
        while (in.hasNextLine()) {
            final String line = in.nextLine();
            builder.append(line);
            builder.append('\n');
        }
        return builder.toString();
    }

}
