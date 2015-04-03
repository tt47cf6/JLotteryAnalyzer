package util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * This class is a FileFilter for use with a JFileChooser. This filter accepts
 * only those files who are either a directory or end with .txt
 * 
 * @author Robert
 */
public final class TextFilter extends FileFilter {

	/**
	 * Returns true if the given file is a directory or a text file.
	 * 
	 * @param file
	 *            the file to test
	 * @return true iff a directory or text file
	 */
	@Override
	public boolean accept(final File file) {
		return file.isDirectory() || file.getName().endsWith("txt");
	}

	/**
	 * Gives a simple description of this filter
	 * 
	 * @return a description of this filter
	 */
	@Override
	public String getDescription() {
		return "Text Files Only";
	}

}
