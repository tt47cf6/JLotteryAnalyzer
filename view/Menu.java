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

/**
 * This class is the Menu for the program. The menu itself has a number of
 * features. Some of these features change the way number are analyzed in the
 * application, and thus are disabled while the user is viewing results of a
 * previous analysis.
 * 
 * @author Robert
 */
public final class Menu extends JMenuBar {

	/**
	 * A static FileChooser to remember where the last place is that the user
	 * saved a results output.
	 */
	private static final JFileChooser SAVE_AS = new JFileChooser();
	// TODO make the save output more verbose

	/**
	 * A list of buttons in this Menu that should be disabled while the user is
	 * viewing results.
	 */
	private final List<JMenuItem> myButtonsToDisable;

	/**
	 * Creates and adds all the elements to this Menu.
	 * 
	 * @param frame
	 *            a reference to the GUI
	 * @param settings
	 *            the global settings
	 */
	public Menu(final JFrame frame, final Settings settings) {
		myButtonsToDisable = new ArrayList<JMenuItem>();
		SAVE_AS.setFileFilter(new TextFilter());
		add(createFileMenu(frame, settings));
		add(createOptionsMenu(frame, settings));
		add(createHelpMenu(frame));
	}

	/**
	 * This method is called when the user changes from the settings panel to
	 * the results view or vise versa. At such time, buttons that need to be
	 * enabled or disabled should be done so.
	 * 
	 * @param flag
	 *            true to enable buttons, false to disable
	 */
	public void setButtonsEnabled(final boolean flag) {
		for (JMenuItem item : myButtonsToDisable) {
			item.setEnabled(flag);
		}
	}

	/**
	 * This method creates the File menu. Error handling for saving the output
	 * is done here.
	 * 
	 * @param frame
	 *            a reference to the GUI
	 * @param settings
	 *            the global settings
	 * @return the file menu
	 */
	private JMenu createFileMenu(final JFrame frame, final Settings settings) {
		final JMenu fileMenu = new JMenu("File");
		final JMenuItem saveAs = new JMenuItem("Save As");
		final JMenuItem updateButton = new JMenuItem("Update");
		myButtonsToDisable.add(updateButton);
		final JMenuItem exit = new JMenuItem("Exit");
		// save as behavior
		saveAs.setToolTipText("Saves the current results as a .txt file");
		saveAs.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent ev) {
				if (JFileChooser.APPROVE_OPTION == SAVE_AS
						.showSaveDialog(frame)) {
					try {
						// correct file name from user input
						String filename = SAVE_AS.getSelectedFile()
								.getAbsolutePath();
						filename = Directory.removeFileExtension(filename)
								+ ".txt";
						// output results
						final PrintStream out = new PrintStream(filename);
						out.println(settings);
						out.println(Calculator.getResult(settings));
						out.close();
					} catch (final FileNotFoundException ex) {
						JOptionPane.showMessageDialog(frame,
								"File could not be written!", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		// update button behavior
		updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent theEvent) {
				final Update updater = new Update(frame);
				setButtonsEnabled(false);
				updater.execute();

			}
		});
		updateButton.setToolTipText("Updates the databases online");
		// exit button behavior
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				frame.dispatchEvent(new WindowEvent(frame,
						WindowEvent.WINDOW_CLOSING));
			}
		});
		// add components to the menu
		fileMenu.add(saveAs);
		fileMenu.add(updateButton);
		fileMenu.add(exit);
		return fileMenu;
	}

	/**
	 * This method creates the Options menu. Most of these buttons somehow
	 * affect the analysis of games and need to be disabled in the results view.
	 * 
	 * @param frame
	 *            a reference to the GUI
	 * @param settings
	 *            global settings
	 * @return the options menu
	 */
	private JMenu createOptionsMenu(final JFrame frame, final Settings settings) {
		final JMenu editMenu = new JMenu("Options");

		// jackpot only selection
		final JCheckBoxMenuItem jackpotOnly = new JCheckBoxMenuItem(
				"Use Only Winning Jackpot Numbers");
		myButtonsToDisable.add(jackpotOnly);
		jackpotOnly
				.setToolTipText("Only Draws that won the jackpot are counted");
		jackpotOnly.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				settings.put(Settings.JACKPOT_ONLY, jackpotOnly.isSelected());
			}
		});
		jackpotOnly.setSelected((Boolean) settings.get(Settings.JACKPOT_ONLY));

		// wheel the results selection
		final JCheckBoxMenuItem wheelOut = new JCheckBoxMenuItem("Wheel Result");
		myButtonsToDisable.add(wheelOut);
		wheelOut.setSelected((Boolean) settings.get(Settings.WHEEL_RESULTS));
		wheelOut.setToolTipText("Output the results as individual draws");
		wheelOut.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				settings.put(Settings.WHEEL_RESULTS, wheelOut.isSelected());
			}
		});

		// TODO output odds of result numbers

		// output raw percentage data
		final JCheckBoxMenuItem rawData = new JCheckBoxMenuItem(
				Settings.RAW_DATA);
		rawData.setToolTipText("Show the popularity of the numbers on calculation");
		rawData.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent theEvent) {
				settings.put(Settings.RAW_DATA, rawData.isSelected());
			}
		});
		myButtonsToDisable.add(rawData);
		rawData.setSelected((Boolean) settings.get(Settings.RAW_DATA));

		// payout amount selection
		final JMenuItem paidOver = new JMenuItem(
				"Use Only Numbers That Paid Above...");
		myButtonsToDisable.add(paidOver);
		paidOver.setToolTipText("Only use draws that paid out over this amount...");
		paidOver.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				final Long previous = (Long) settings
						.get(Settings.PAID_OVER_AMOUNT);
				final String input = JOptionPane.showInputDialog(frame,
						"Only use draws that paid out above:", previous);
				settings.put(Settings.PAID_OVER_AMOUNT,
						util.NumberUtils.parseLong(input));
			}
		});

		// custom numbers input
		final JMenuItem customNums = new JMenuItem(
				"Numbers to Always Include...");
		myButtonsToDisable.add(customNums);
		customNums
				.setToolTipText("Your lucky numbers to include in each calculation");
		customNums.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				final Set<Integer> previousNums = (Set<Integer>) settings
						.get(Settings.CUSTOM_NUMBERS);
				final String previousInput = util.NumberUtils
						.setAsString(previousNums);
				final String input = JOptionPane
						.showInputDialog(
								frame,
								"Always include these numbers: (Separate with a space)",
								previousInput);
				if (input != null) {
					settings.put(Settings.CUSTOM_NUMBERS,
							util.NumberUtils.parseCustomNumbers(input));
				}
			}
		});

		// add components
		editMenu.add(jackpotOnly);
		editMenu.add(wheelOut);
		editMenu.add(rawData);
		editMenu.add(paidOver);
		editMenu.add(customNums);
		return editMenu;
	}

	/**
	 * This method creates the help menu with an option to view
	 * either text contained in the help file, or online help via the
	 * GitHub wiki.
	 * 
	 * @param frame a reference to the GUI
	 * @return the help menu
	 */
	private JMenu createHelpMenu(final JFrame frame) {
		final JMenu helpMenu = new JMenu("Help");
		final JMenuItem helpButton = new JMenuItem("Help");
		// TODO add button to go to GitHub wiki
		helpButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				String text;
				try {
					final Scanner in = new Scanner(new File("help.help"));
					text = Directory.textFileAsString(in);
				} catch (FileNotFoundException e1) {
					text = "Help file not found!";
				}
				JOptionPane.showMessageDialog(frame, text);
			}
		});
		helpMenu.add(helpButton);
		return helpMenu;
	}

}
