
package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import controller.Settings;

public final class CenterPanel extends JPanel {
    
    /**
     * 
     */
    private static final long serialVersionUID = 5402386247981638008L;
    private final ResultsPane myResultsPane;

    public CenterPanel(final Settings theSettings) {
        super(new CardLayout());
        myResultsPane = new ResultsPane();
        setup(theSettings);
    }

    private void setup(final Settings theSettings) {
        add(new SettingsPanel(theSettings));
        add(myResultsPane);
    }

    public void nextCard() {
        final CardLayout cards = (CardLayout) getLayout();
        cards.next(this);
    }
    
    public void setResultsText(final String theText) {
        myResultsPane.setText(theText);
    }

    private class ResultsPane extends JPanel {
        
        /**
         * 
         */
        private static final long serialVersionUID = -4814805169736021659L;
        private final JTextArea myTextArea;

        public ResultsPane() {
            super(new BorderLayout());
            myTextArea = new JTextArea("ERROR!");
            myTextArea.setEditable(false);
            final JScrollPane textWithScroll = new JScrollPane(myTextArea);
            textWithScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            add(textWithScroll, BorderLayout.CENTER);
        }
        
        public void setText(final String theText) {
            myTextArea.setText(theText);
        }
    }
}
