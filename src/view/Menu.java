
package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import updater.Update;
import util.Directory;
import util.TextFilter;
import controller.Calculator;
import controller.Settings;

public final class Menu extends JMenuBar {
    
    /**
     * 
     */
    private static final long serialVersionUID = -5185725477834757260L;

    private static final JFileChooser SAVE_AS = new JFileChooser();
    
    private final List<JMenuItem> myButtonsToDisable;

    public Menu(final JFrame theFrame, final Settings theSettings) {
        myButtonsToDisable = new ArrayList<JMenuItem>();
        SAVE_AS.setFileFilter(new TextFilter());
        add(createFileMenu(theFrame, theSettings));
        add(createEditMenu(theFrame, theSettings));
        add(createHelpMenu(theFrame));
    }
    
    public void setButtonsEnabled(final boolean theFlag) {
        for (JMenuItem item : myButtonsToDisable) {
            item.setEnabled(theFlag);
        }
    }

    private JMenu createFileMenu(final JFrame theFrame, final Settings theSettings) {
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem saveAs = new JMenuItem("Save As");
        final JMenuItem updateButton = new JMenuItem("Update");
        myButtonsToDisable.add(updateButton);
        final JMenuItem exit = new JMenuItem("Exit");
        saveAs.setToolTipText("Saves the current results as a .doc file");
        saveAs.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent theEvent) {
                if (JFileChooser.APPROVE_OPTION == SAVE_AS.showSaveDialog(theFrame)) {
                    try {
                        String filename = SAVE_AS.getSelectedFile().getAbsolutePath();
                        filename = Directory.removeFileExtension(filename) + ".txt";
                        final PrintStream out = new PrintStream(filename);
                        out.println(theSettings);
                        out.println(Calculator.getResult(theSettings));
                        out.close();
                    } catch (FileNotFoundException e) {
                        JOptionPane.showMessageDialog(theFrame, "File could not be written!", 
                                                      "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent theEvent) {
                final Update updater = new Update(theFrame);
                setButtonsEnabled(false);
                updater.execute();
                
            }
        });
        updateButton.setToolTipText("Updates the databases online");
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                theFrame.dispatchEvent(new WindowEvent(theFrame, WindowEvent.WINDOW_CLOSING));
            }
        });
        fileMenu.add(saveAs);
        fileMenu.add(updateButton);
        fileMenu.add(exit);
        return fileMenu;
    }

    private JMenu createEditMenu(final JFrame theFrame, final Settings theSettings) {
        final JMenu editMenu = new JMenu("Edit");
        final JCheckBoxMenuItem jackpotOnly =
                        new JCheckBoxMenuItem("Use Only Winning Jackpot Numbers");
        myButtonsToDisable.add(jackpotOnly);
        jackpotOnly.setToolTipText("Only Draws that won the jackpot are counted");
        jackpotOnly.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                theSettings.put(Settings.JACKPOT_ONLY, jackpotOnly.isSelected());
            }
        });
        jackpotOnly.setSelected((Boolean) theSettings.get(Settings.JACKPOT_ONLY));
        final JCheckBoxMenuItem wheelOut = new JCheckBoxMenuItem("Wheel Result");
        myButtonsToDisable.add(wheelOut);
        wheelOut.setSelected((Boolean) theSettings.get(Settings.WHEEL_RESULTS));
        wheelOut.setToolTipText("Output the results as individual draws");
        wheelOut.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                theSettings.put(Settings.WHEEL_RESULTS, wheelOut.isSelected());
            }
        });
        final JCheckBoxMenuItem rawData = new JCheckBoxMenuItem(Settings.RAW_DATA);
        rawData.setToolTipText("Show the popularity of the numbers on calculation");
        rawData.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent theEvent) {
                theSettings.put(Settings.RAW_DATA, rawData.isSelected());
            }
        });
        myButtonsToDisable.add(rawData);
        rawData.setSelected((Boolean) theSettings.get(Settings.RAW_DATA));
        final JMenuItem paidOver = new JMenuItem("Use Only Numbers That Paid Above...");
        myButtonsToDisable.add(paidOver);
        paidOver.setToolTipText("Only use draws that paid out over this amount...");
        final JMenuItem customNums = new JMenuItem("Numbers to Always Include...");
        myButtonsToDisable.add(customNums);
        paidOver.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final Long previous = (Long) theSettings.get(Settings.PAID_OVER_AMOUNT);
                final String input = JOptionPane.showInputDialog(theFrame, 
                                         "Only use draws that paid out above:", previous);
                theSettings.put(Settings.PAID_OVER_AMOUNT, util.NumberUtils.parseLong(input));
            }
        });
        customNums.setToolTipText("Your lucky numbers to include in each calculation");
        customNums.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final Set<Integer> previousNums = 
                                (Set<Integer>) theSettings.get(Settings.CUSTOM_NUMBERS);
                final String previousInput = util.NumberUtils.setAsString(previousNums);
                final String input = JOptionPane.showInputDialog(theFrame, 
                                         "Always include these numbers: (Separate with a space)",
                                         previousInput);
                if (input != null) {
                    theSettings.put(Settings.CUSTOM_NUMBERS, util.NumberUtils.parseCustomNumbers(input));
                }
            }
        });
        editMenu.add(jackpotOnly);
        editMenu.add(wheelOut);
        editMenu.add(rawData);
        editMenu.add(paidOver);
        editMenu.add(customNums);
        return editMenu;
    }

    private JMenu createHelpMenu(final JFrame theFrame) {
        final JMenu helpMenu = new JMenu("Help");
        final JMenuItem helpButton = new JMenuItem("Help");
        final JMenuItem aboutButton = new JMenuItem("About");
        helpButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                String text;
                try {
                    final Scanner in = new Scanner(new File("help.help"));
                    text = Directory.textFileAsString(in);
                } catch (FileNotFoundException e1) {
                    text = "Help file not found!";
                }
                JOptionPane.showMessageDialog(theFrame, text);
            }
        });
        aboutButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                JOptionPane.showMessageDialog(theFrame, "(c) Robert Ogden 2014"
                                              + "\nAll Rights Reserved", "Credits",
                                              JOptionPane.PLAIN_MESSAGE);
            }
        });
        helpMenu.add(helpButton);
        helpMenu.add(aboutButton);
        return helpMenu;
    }

}
