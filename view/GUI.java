package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import controller.Calculator;
import controller.Settings;

/**
 * This class is the main start class for the application. This creates and
 * instantiates all the subelements with appropriate error handling for the
 * serialized global settings object.
 * 
 * @author Robert
 */
public final class GUI {
	/**
	 * The screen size of the user's machine. Used for centering the window.
	 */
	public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit()
			.getScreenSize();

	/**
	 * The filename for serializing the settings.
	 */
	private static final String SETTINGS_SAVE_FILE = "settings.romf";

	/**
	 * The filename for the icon image for the application.
	 */
	private static final String ICON_FILE = "clover.gif";

	/**
	 * The button text for starting an analysis.
	 */
	private static final String CALCULATE = "Calculate";

	/**
	 * The button text for returning to the settings view from the results view.
	 */
	private static final String BACK = "Back";

	/**
	 * The JFrame for the application.
	 */
	private final JFrame myFrame;

	/**
	 * The global settings.
	 */
	private Settings mySettings;

	/**
	 * A reference to the center sub panel of the GUI.
	 */
	private final CenterPanel myCenterPanel;

	/**
	 * A reference to the menu.
	 */
	private final Menu myMenu;

	/**
	 * Constructs the GUI, reading in the serialized settings file, and
	 * initalizes the various objects.
	 */
	private GUI() {
		myFrame = new JFrame();
		try {
			// deserialize
			final FileInputStream fileIn = new FileInputStream(
					SETTINGS_SAVE_FILE);
			final ObjectInputStream in = new ObjectInputStream(fileIn);
			mySettings = (Settings) in.readObject();
			in.close();
			fileIn.close();
		} catch (final Exception e) {
			// if there was a problem, just use the default settings
			mySettings = Settings.DEFAULT;
		}
		myCenterPanel = new CenterPanel(mySettings);
		myMenu = new Menu(myFrame, mySettings);
	}

	/**
	 * Populates the JFrame and defines it default behavior.
	 */
	private void start() {
		myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		myFrame.addWindowListener(new WindowAdapter() {
			// on close, serialize the settings
			public void windowClosing(final WindowEvent theEvent) {
				try {
					final FileOutputStream fileOut = new FileOutputStream(
							SETTINGS_SAVE_FILE);
					final ObjectOutputStream out = new ObjectOutputStream(
							fileOut);
					out.writeObject(mySettings);
					out.close();
					fileOut.close();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		});
		myFrame.setIconImage(new ImageIcon(ICON_FILE).getImage());
		// set up card layout
		myFrame.setJMenuBar(myMenu);
		myFrame.add(myCenterPanel, BorderLayout.CENTER);
		final JButton switchButton = new JButton(CALCULATE);
		switchButton.addActionListener(new SwitchButtonListener());
		myFrame.add(switchButton, BorderLayout.SOUTH);
		// start
		myFrame.pack();
		centerWindow();
		myFrame.setResizable(false);
		myFrame.setVisible(true);
	}

	/**
	 * Using the constant SCREEN_SIZE, this centers the window on the screen. It
	 * does so by assessing the extra space in both dimensions and then placing
	 * the window where there is an equal amount on either side in both
	 * dimensions.
	 */
	private void centerWindow() {
		final int extraVerticalSpace = SCREEN_SIZE.height - myFrame.getHeight();
		final int extraHorizontalSpace = SCREEN_SIZE.width - myFrame.getWidth();
		myFrame.setLocation(extraHorizontalSpace / 2, extraVerticalSpace / 2);
	}

	/**
	 * Main start method. Thread safe.
	 * 
	 * @param args
	 *            unused command line arguments
	 */
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final GUI gui = new GUI();
				gui.start();
			}
		});
	}

	/**
	 * This class is the ActionListener for when the user clicks the 'Calculate'
	 * or 'Back' button at the bottom of the application. This button cues the
	 * analysis of the selected parameters.
	 * 
	 * @author Robert
	 */
	private class SwitchButtonListener implements ActionListener {

		/**
		 * The results of the last run are always saved to a file for use away
		 * from the computer. This is the filename for that file.
		 */
		private static final String SAVE_FILE = "last_results.txt";

		/**
		 * When the button is clicked, this method is called. If we were viewing
		 * the configuration panel, run the analysis, switch the the results
		 * view, and then display the results. Else, go back to the
		 * configuration panel.
		 * 
		 * @param event
		 *            the action event. Used to get a reference to the button
		 */
		public void actionPerformed(final ActionEvent event) {
			try {
				// user input validation
				if (!mySettings.anAlgorithmSelected()) {
					throw new NullPointerException();
				}
				final JButton button = (JButton) event.getSource();
				if (button.getText().equals(CALCULATE)) {
					// we were on the settings view
					// compute results
					final String results = Calculator.getResult(mySettings);
					// save and display
					saveToFile(mySettings.toString() + "\n\n" + results);
					myCenterPanel.setResultsText(results);
					button.setText(BACK);
					myMenu.setButtonsEnabled(false);
				} else {
					// go back to the settings display
					button.setText(CALCULATE);
					myMenu.setButtonsEnabled(true);
				}
				// switch views
				myCenterPanel.nextCard();
			} catch (final FileNotFoundException e) {
				// this is only thrown if the database file can't be read
				// TODO is the database read everytime? Fix that silly
				JOptionPane.showMessageDialog(myFrame,
						"Database File Not Found!", "Error",
						JOptionPane.ERROR_MESSAGE);
			} catch (final NullPointerException e) {
				// this is only thrown if the user doesn't select an algorithm
				JOptionPane.showMessageDialog(myFrame,
						"Select the parameters first", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}

		/**
		 * Saves the given text to the last results save file. Error handling is
		 * done inside this method.
		 * 
		 * @param text
		 *            the text to save to the file
		 */
		private void saveToFile(final String text) {
			try {
				final PrintStream out = new PrintStream(SAVE_FILE);
				out.print(text);
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

}
