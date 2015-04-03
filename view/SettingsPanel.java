package view;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import lotto.Lottery;
import controller.Settings;

/**
 * This Panel is the main panel that users interact with to specify basic
 * behavior of a analysis. It is made up of several different sub panels, each
 * with their own respective behavior and settings to change. When this object
 * is first created, all selections are made to reflect the values already in
 * the settings object. This allows for persistence between runs.
 * 
 * @author Robert
 */
public final class SettingsPanel extends JPanel {

	/**
	 * The vertical spacing inbetween elements in the view.
	 */
	private static final int VERT_SPACING = 15;

	/**
	 * Instantiates a new SettingsPanel and adds all of the elements to it. A
	 * settings object is needed to put changes to when a user selects a
	 * different option.
	 * 
	 * @param settings
	 *            the global settings object
	 */
	public SettingsPanel(final Settings settings) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setup(settings);
	}

	/**
	 * Adds all the content to this panel with spacing.
	 * 
	 * @param settings
	 *            passes on the settings to factory methods
	 */
	private void setup(final Settings settings) {
		// game chooser
		add(createGameChooserPanel(settings));
		add(Box.createVerticalStrut(VERT_SPACING));

		// number of draws
		add(createDrawChooserPanel(settings));
		add(Box.createVerticalStrut(VERT_SPACING));

		// inclusive or exclusive selection
		add(createInclusiveExclusivePanel(settings));
		add(Box.createVerticalStrut(VERT_SPACING));

		// various algorithms
		add(createAlgorithmsPanel(settings));

	}

	/**
	 * This method creates a panel to choose which Lottery game to analyze. Each
	 * option is a radio button which must all be exclusive of each other.
	 * 
	 * @param settings
	 *            the global settings
	 * @return a JPanel to choose the game to analyze
	 */
	private static JPanel createGameChooserPanel(final Settings settings) {
		final JPanel gameChooser = new JPanel(new GridLayout(4, 1));
		final ButtonGroup group = new ButtonGroup();
		gameChooser.add(new JLabel("Game to Analyze", JLabel.CENTER));
		// the games to analyze
		gameChooser.add(createGameButton(group, Lottery.WA_LOTTO, settings));
		gameChooser
				.add(createGameButton(group, Lottery.MEGA_MILLIONS, settings));
		gameChooser.add(createGameButton(group, Lottery.POWERBALL, settings));
		return gameChooser;
	}

	/**
	 * A helper method to the above method that returns a single JRabioButton in
	 * the given group, with the given display text, and that puts the given
	 * text value in the SELECTED_GAME field of the settings.
	 * 
	 * @param group
	 *            the button group to add to
	 * @param text
	 *            the text to display and put into the map
	 * @param settings
	 *            the global settings
	 * @return a new button with the given text and properties
	 */
	private static JRadioButton createGameButton(final ButtonGroup group,
			final String text, final Settings settings) {
		final JRadioButton button = new JRadioButton(text);
		group.add(button);
		// action listener puts the text as the value for SELECTED_GAME
		button.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				settings.put(Settings.SELECTED_GAME, text);
			}
		});
		// set inital state
		button.setSelected(((String) settings.get(Settings.SELECTED_GAME))
				.equals(text));
		// tooltip
		button.setToolTipText("Selects " + text + " to be analyzed.");
		return button;
	}

	/**
	 * This method creates the panel where the user can choose how many draws to
	 * analyze. This value will be bound checked outside of this method. Every
	 * time the focus leaves the input text field, the number will be checked.
	 * 
	 * @param settings
	 *            the global settings
	 * @return a panel to choose the number of draws to count
	 */
	private static JPanel createDrawChooserPanel(final Settings settings) {
		final JPanel drawChooser = new JPanel(new GridLayout(2, 1));
		drawChooser.add(new JLabel("# of Draws to Count", JLabel.CENTER));
		// subpanel used to put the input field and help label together
		final JPanel subPanel = new JPanel();
		// set initial value
		final int startValue = (Integer) settings.get(Settings.COUNT);
		final JTextField inputField = new JTextField("" + startValue, 3);
		inputField
				.setToolTipText("The number of previous draws to count matching"
						+ " your criteria");
		// listener for completed user change to the number
		inputField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(final FocusEvent e) {
				// do nothing
			}

			@Override
			public void focusLost(final FocusEvent e) {
				try {
					final int input = Integer.parseInt(inputField.getText());
					inputField.setText("" + input);
					settings.put(Settings.COUNT, input);
				} catch (final NumberFormatException ex) {
					JOptionPane.showMessageDialog(null, "Bad Input", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		subPanel.add(inputField);
		subPanel.add(new JLabel("0 for all"));
		drawChooser.add(subPanel);
		return drawChooser;
	}

	/**
	 * This method creates a panel for the user to choose whether the selected
	 * algorithms should be inclusive or exclusive when outputting the final
	 * result.
	 * 
	 * @param settings
	 *            the global settings
	 * @return a panel to select whether algorithms are inclusive
	 */
	private static JPanel createInclusiveExclusivePanel(final Settings settings) {
		final JPanel panel = new JPanel(new GridLayout(3, 1));
		panel.add(new JLabel("Algorithms Should", JLabel.CENTER));
		final ButtonGroup group = new ButtonGroup();

		// inclusive button
		final JRadioButton inclusiveButton = new JRadioButton("Be Inclusive");
		inclusiveButton
				.setToolTipText("Each number from each selected algorithim is used"
						+ " in final display");
		inclusiveButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				settings.put(Settings.INCLUSIVE, true);
			}
		});
		// set initial state
		inclusiveButton.setSelected((Boolean) settings.get(Settings.INCLUSIVE));
		group.add(inclusiveButton);
		panel.add(inclusiveButton);

		// exclusive button
		final JRadioButton exclusiveButton = new JRadioButton("Be exclusive");
		exclusiveButton
				.setToolTipText("Only numbers occuring in every algorithim's result"
						+ " is used in the final display");
		exclusiveButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				settings.put(Settings.INCLUSIVE, false);
			}
		});
		// set inital state
		exclusiveButton.setSelected(!inclusiveButton.isSelected());

		group.add(exclusiveButton);
		panel.add(exclusiveButton);
		return panel;
	}

	/**
	 * This method creates a panel to select which algorithm(s) should be used
	 * in the analysis. Multiple algorithms may be selected, hence the use of
	 * checkboxes.
	 * 
	 * @param settings
	 *            the global settings
	 * @return a panel to select which algorithms should be analyzed
	 */
	private static JPanel createAlgorithmsPanel(final Settings settings) {
		final JPanel algorithms = new JPanel(new GridLayout(4, 1));
		algorithms.add(new JLabel("Algorithms", JLabel.CENTER));

		// hot numbers
		final JCheckBox hotButton = createAlgorithmsBox(settings,
				Settings.HOT_SELECTED);
		hotButton.setToolTipText("Numbers that are frequently drawn.");
		algorithms.add(hotButton);

		// cold numbers
		final JCheckBox coldButton = createAlgorithmsBox(settings,
				Settings.COLD_SELECTED);
		coldButton.setToolTipText("Numbers that are not frequently drawn.");
		algorithms.add(coldButton);

		// periodic numbers
		final JCheckBox perdButton = createAlgorithmsBox(settings,
				Settings.PERIODIC_SELECTED);
		perdButton
				.setToolTipText("In a truly random game, numbers will appear every certain"
						+ " number of drawings. This goes back that far and returns"
						+ " those numbers.");
		algorithms.add(perdButton);

		return algorithms;
	}

	/**
	 * A helper method to create a single checkbox for a given algorithm, that
	 * displays the given text. This text must be the key for the settings
	 * object for the respective game.
	 * 
	 * @param settings
	 *            the global settings object
	 * @param text
	 *            the text to dispaly and key to the settings
	 * @return a check box to select an algorithm
	 */
	private static JCheckBox createAlgorithmsBox(final Settings settings,
			final String text) {
		final JCheckBox button = new JCheckBox(text);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				settings.put(text, button.isSelected());
			}
		});
		button.setSelected((Boolean) settings.get(text));
		return button;
	}
}
