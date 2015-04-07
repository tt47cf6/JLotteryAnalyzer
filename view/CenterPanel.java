package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import controller.Settings;

/**
 * This class is the main content panel of the GUI. It is a simple wrapper class
 * for two sub panels, SettingsPanel and ResultsPanel, contained in a
 * CardLayout. When cued by the GUI, this panel advances to the next card. When
 * switching to the results pane, the text is cleared and it is the job of the
 * GUI to update the text when the results have been calculated.
 * 
 * @author Robert
 */
public final class CenterPanel extends JPanel {

	/**
	 * A reference to the results panel to set the text of the results.
	 */
	private final ResultsPanel myResultsPanel;

	/**
	 * Constructs and populates this panel.
	 * 
	 * @param settings
	 *            a reference to the settings to pass to the settings panel
	 */
	public CenterPanel(final Settings settings) {
		super(new CardLayout());
		myResultsPanel = new ResultsPanel();
		add(new SettingsPanel(settings));
		add(myResultsPanel);
	}

	/**
	 * Advances the panel to show the next card. It is assumed there are only
	 * two cards, and so this method will simply switch between them.
	 */
	public void nextCard() {
		myResultsPanel.setText(ResultsPanel.START_TEXT);
		final CardLayout cards = (CardLayout) getLayout();
		cards.next(this);
	}

	/**
	 * Sets the display text in the ResultsPanel.
	 * 
	 * @param text
	 *            the text to display
	 */
	public void setResultsText(final String text) {
		myResultsPanel.setText(text);
	}

	/**
	 * A simple wrapper class for a JTextArea to display the results text.
	 * 
	 * @author Robert
	 */
	private class ResultsPanel extends JPanel {

		/**
		 * Default text to show until the panel is updated.
		 */
		public static final String START_TEXT = "Computing...";

		/**
		 * A reference to the JTextArea element.
		 */
		private final JTextArea myTextArea;

		/**
		 * Constructs the panel, sets up the text area, and adds it to the
		 * panel.
		 */
		public ResultsPanel() {
			super(new BorderLayout());
			myTextArea = new JTextArea(START_TEXT);
			// set text area properties
			myTextArea.setEditable(false);
			myTextArea.setWrapStyleWord(true);
			myTextArea.setLineWrap(true);
			// add into a scroll pane
			final JScrollPane textWithScroll = new JScrollPane(myTextArea);
			textWithScroll
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			// add to panel
			add(textWithScroll, BorderLayout.CENTER);
		}

		/**
		 * Sets the text of the text area to the passed value
		 * 
		 * @param text
		 *            the text to display
		 */
		public void setText(final String text) {
			myTextArea.setText(text);
		}
	}
}
