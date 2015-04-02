
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
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import lotto.Lottery;
import controller.Settings;

public final class SettingsPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -3129612690864373781L;
    private static final int VERT_SPACING = 15;

    public SettingsPanel(final Settings theSettings) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setup(theSettings);
    }

    private void setup(final Settings theSettings) {
        add(createGameChooserPanel(theSettings));
        add(Box.createVerticalStrut(VERT_SPACING));
        add(createDrawChooserPanel(theSettings));
        add(Box.createVerticalStrut(VERT_SPACING));
        add(createAddSubtractPanel(theSettings));
        add(Box.createVerticalStrut(VERT_SPACING));
        add(createAlgorithmsPanel(theSettings));

    }

    private JPanel createGameChooserPanel(Settings theSettings) {
        final JPanel gameChooser = new JPanel(new GridLayout(4, 1));
        final ButtonGroup group = new ButtonGroup();
        gameChooser.add(new JLabel("Game to Analyze", JLabel.CENTER));
        gameChooser.add(createGameButton(group, Lottery.WA_LOTTO, theSettings));
        gameChooser.add(createGameButton(group, Lottery.MEGA_MILLIONS, theSettings));
        gameChooser.add(createGameButton(group, Lottery.POWERBALL, theSettings));
        return gameChooser;
    }

    private JRadioButton createGameButton(final ButtonGroup theGroup, final String theText,
                                          final Settings theSettings) {
        final JRadioButton button = new JRadioButton(theText);
        theGroup.add(button);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent theEvent) {
                theSettings.put(Settings.SELECTED_GAME, theText);
            }
        });
        button.setSelected(((String) theSettings.get(Settings.SELECTED_GAME)).equals(theText));
        button.setToolTipText("Selects " + theText + " to be analyzed.");
        return button;
    }

    private JPanel createDrawChooserPanel(final Settings theSettings) {
        final JPanel drawChooser = new JPanel(new GridLayout(2, 1));
        drawChooser.add(new JLabel("# of Draws to Count", JLabel.CENTER));
        final JPanel subPanel = new JPanel();
        final int startValue = (Integer) theSettings.get(Settings.COUNT);
        final JTextField inputField = new JTextField("" + startValue, 3);
        inputField.setToolTipText("The number of previous draws to count matching"
                                  + " your criteria");
        inputField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(final FocusEvent theEvent) {
                // do nothing
            }
            @Override
            public void focusLost(final FocusEvent theEvent) {
                final int input = util.NumberUtils.parseInteger(inputField.getText());
                inputField.setText("" + input);
                theSettings.put(Settings.COUNT, input); 
            }
        });
        subPanel.add(inputField);
        subPanel.add(new JLabel("0 for all"));
        drawChooser.add(subPanel);
        return drawChooser;
    }
    
    

    private JPanel createAddSubtractPanel(final Settings theSettings) {
        final JPanel addSubtractPanel = new JPanel(new GridLayout(2, 1));
        addSubtractPanel.add(new JLabel("Algorithms Should", JLabel.CENTER));
        final JPanel subPanel = new JPanel();
        final ButtonGroup group = new ButtonGroup();
        final JRadioButton addButton = new JRadioButton("Add");
        addButton.setToolTipText("Each number from each selected algorithim is used" 
                                 + " in final display");
        addButton.addActionListener(new ActionListener() {
           public void actionPerformed(final ActionEvent theEvent) {
               theSettings.put(Settings.ADD_RESULTS, true);
           }
        });
        addButton.setSelected((Boolean) theSettings.get(Settings.ADD_RESULTS));
        group.add(addButton);
        subPanel.add(addButton);
        final JRadioButton subtractButton = new JRadioButton("Subtract");
        subtractButton.setToolTipText("Only numbers occuring in every algorithim's result"
                                      + " is used in the final display");
        subtractButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent theEvent) {
                theSettings.put(Settings.ADD_RESULTS, false);
            }
         });
        subtractButton.setSelected(!addButton.isSelected());
        group.add(subtractButton);
        subPanel.add(subtractButton);
        addSubtractPanel.add(subPanel);
        return addSubtractPanel;
    }

    private JPanel createAlgorithmsPanel(Settings theSettings) {
        final JPanel algorithms = new JPanel(new GridLayout(4, 1));
        algorithms.add(new JLabel("Algorithms", JLabel.CENTER));
        final JCheckBox hotButton = createAlgorithmsButton(theSettings, Settings.HOT_SELECTED);
        hotButton.setToolTipText("Numbers that are frequently drawn.");
        algorithms.add(hotButton);
        final JCheckBox coldButton = createAlgorithmsButton(theSettings, Settings.COLD_SELECTED);
        coldButton.setToolTipText("Numbers that are not frequently drawn.");
        algorithms.add(coldButton);
        final JCheckBox perdButton = createAlgorithmsButton(theSettings, Settings.PERIODIC_SELECTED);
        perdButton.setToolTipText("In a truly random game, numbers will appear every certain" 
                                  + " number of drawings. This goes back that far and uses"
                                  + " those numbers again for the specified game");
        algorithms.add(perdButton);
        return algorithms;
    }
    
    private JCheckBox createAlgorithmsButton(final Settings theSettings, final String theText) {
        final JCheckBox button = new JCheckBox(theText);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent theEvent) {
                theSettings.put(theText, button.isSelected());
            }
        });
        button.setSelected((Boolean) theSettings.get(theText));
        return button;
    }
}
