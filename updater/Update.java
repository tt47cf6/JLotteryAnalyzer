package updater;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import analyzer.Draw;
import lotto.Lottery;
import lotto.MegaMillions;
import lotto.PowerBall;
import lotto.WALotto;
import view.Menu;

/**
 * This class is a SwingWorker designed to work in the background to update all
 * games' databases. This class utilizes a JDialog to post information about the
 * current status of the update to the user.
 * 
 * @author Robert
 */
public final class Update extends SwingWorker<Integer, String> {

	/**
	 * The screen size of the user's machine. Used for centering the window.
	 */
	public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit()
			.getScreenSize();
	/**
	 * The percentage when we are done.
	 */
	private static final int COMPLETE = 100;

	/**
	 * An array of games to update.
	 */
	private static final Lottery[] UPDATE_THESE = { new WALotto(),
			new MegaMillions(), new PowerBall() };

	/**
	 * A reference to the parent GUI.
	 */
	private final JFrame myFrame;

	/**
	 * My dialog to display status information to the user.
	 */
	private final JDialog myDialog;

	/**
	 * The label in the JDialog that displays the progress.
	 */
	private final JLabel myLabel;

	/**
	 * The button the user can click to exit the JDialog, but only when the
	 * update is complete.
	 */
	private final JButton myButton;

	/**
	 * Set when the update is done and the updater can be closed.
	 */
	private boolean okayToExit;

	/**
	 * Sets up this class, creates, and populates the JDialog.
	 * 
	 * @param frame
	 *            the parent JFrame
	 */
	public Update(final JFrame frame) {
		myFrame = frame;
		myDialog = new JDialog(frame, "Update");
		myLabel = new JLabel();
		myButton = new JButton("Exit");
		setup();
	}

	/**
	 * Populate the JDialog with a simple layout, but don't display it yet.
	 */
	private void setup() {
		myButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent theEvent) {
				if (okayToExit) {
					myDialog.dispose();
				}
			}
		});
		final JPanel panel = new JPanel(new GridLayout(2, 1));
		panel.add(myLabel);
		panel.add(myButton);
		myDialog.add(panel);
		myButton.setEnabled(false);
		myDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		myDialog.pack();

	}

	/**
	 * Sets the text of the label when some amount of progress is completed. The
	 * JDialog is then repacked and recentered on the screen. Then isVisible is
	 * asserted to be true to take of this instance being the first time
	 * progress is posted.
	 * 
	 * @param text
	 *            the text to display
	 */
	private void setDialogText(final String text) {
		myLabel.setText(text);
		myDialog.pack();
		centerDialog();
		myDialog.setVisible(true);
	}

	/**
	 * Using the constant SCREEN_SIZE, this centers the window on the screen. It
	 * does so by assessing the extra space in both dimensions and then placing
	 * the window where there is an equal amount on either side in both
	 * dimensions.
	 */
	private void centerDialog() {
		final int extraVerticalSpace = SCREEN_SIZE.height
				- myDialog.getHeight();
		final int extraHorizontalSpace = SCREEN_SIZE.width
				- myDialog.getWidth();
		myDialog.setLocation(extraHorizontalSpace / 2, extraVerticalSpace / 2);
	}

	/**
	 * The actual work of updating the classes by opening a SingleGameUpdater
	 * object and having that do its work for each game and year that is needed
	 * to be updated.
	 * 
	 * @return not used
	 * @throws any
	 *             exception being thrown by code that was called
	 */
	@Override
	protected Integer doInBackground() throws Exception {
		// post inital info to user
		publish("Setting up Connection");
		setProgress(0);
		final int[] years = getLastYear();
		try {
			// update for each year
			for (int year = 0; year < years.length; year++) {
				// update for each game in that year
				for (int game = 0; game < UPDATE_THESE.length; game++) {
					// publish to user
					publish("Updating " + UPDATE_THESE[game] + " in "
							+ years[year]);
					// run update
					final SingleGameUpdater updater = new SingleGameUpdater(
							UPDATE_THESE[game], years[year]);
					// post info to user
					updater.update();
					setProgress(((game + 1) * COMPLETE) / UPDATE_THESE.length
							- 1);
				}
			}
			publish("Update Successful.");
		} catch (final FileNotFoundException e) {
			// if the database could not be read
			e.printStackTrace();
			publish("File Database Error!");
		} catch (IOException e) {
			// if an internet connection couldn't be opened
			e.printStackTrace();
			publish("Internet Connection Error!");
		} catch (final Exception e) {
			// unknown error
			e.printStackTrace();
			publish("Unknown Fatal Error!");
		}
		setProgress(COMPLETE);
		return 0;
	}

	/**
	 * A thread-safe listener to the publish calls above. The List contains all
	 * messages publish but not yet consumed. In any case, publish just the last
	 * message. If we are done, enabled the button to close the JDialog.
	 */
	@Override
	protected void process(final List<String> list) {
		setDialogText(list.get(list.size() - 1));
		if (getProgress() == COMPLETE) {
			okayToExit = true;
			myButton.setEnabled(true);
			myDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		}
	}

	/**
	 * When the SwingWorker is notified we are done, this method will be called.
	 * During the update, the menu buttons were disabled. Reenable them now.
	 */
	@Override
	protected void done() {
		final Menu menu = (Menu) myFrame.getJMenuBar();
		menu.setButtonsEnabled(true);
	}

	/**
	 * This method runs through all games, gets a list of all years represented
	 * in the database for that game. Then, find the biggest year that is in
	 * common for all databases. If this year is the same as the current year,
	 * just return an array containing this current year. If not, it will
	 * contains every year from this year to the last year in the database
	 * inclusive.
	 * 
	 * @return an array of years to update all games for
	 */
	private int[] getLastYear() {
		int max = Lottery.MIN_COMMON_YEAR;
		// come up with a maximum shared year for all games
		try {
			// find all shared years
			final Set<Integer> commonYears = getYearsFromGame(UPDATE_THESE[0]);
			for (int i = 1; i < UPDATE_THESE.length; i++) {
				commonYears.retainAll(getYearsFromGame(UPDATE_THESE[i]));
			}
			// find the max
			for (int year : commonYears) {
				if (year > max) {
					max = year;
				}
			}
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
			max = Lottery.MIN_COMMON_YEAR;
		}
		// compare with the current year and return a list of years between then
		// and now
		final int[] result;
		if (max == currentYear()) {
			result = new int[1];
			result[0] = max;
		} else {
			result = new int[currentYear() - max + 1];
			int year = max;
			for (int i = 0; i < result.length; i++) {
				result[i] = year;
				year++;
			}
		}
		return result;
	}

	/**
	 * Returns the current calendar year.
	 * 
	 * @return the current calendar year
	 */
	private int currentYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	/**
	 * Reads in the database file and returns a set of all years in the
	 * database.
	 * 
	 * @param game
	 *            the game to read in the database of
	 * @return a set of years for draws in the database
	 * @throws FileNotFoundException
	 *             if the file could not be opened
	 */
	private Set<Integer> getYearsFromGame(final Lottery game)
			throws FileNotFoundException {
		final Set<Integer> result = new HashSet<Integer>();
		final Scanner in = new Scanner(new File(game.dataFile()));
		while (in.hasNextLine()) {
			final String[] split = in.nextLine().split(Draw.DELIMITER);
			result.add(Integer.parseInt(split[2]));
		}
		in.close();
		return result;
	}
}
