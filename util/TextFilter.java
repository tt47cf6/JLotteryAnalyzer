package util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public final class TextFilter extends FileFilter {

    @Override
    public boolean accept(File theFile) {
        final String filename = theFile.getName();
        boolean result = theFile.isDirectory();
        return result || filename.endsWith("txt");
    }

    @Override
    public String getDescription() {
        return "Text Files Only";
    }

}
