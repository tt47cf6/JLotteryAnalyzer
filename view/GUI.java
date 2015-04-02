
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

public final class GUI {
    /** The screen size of the user's machine. Used for centering the window. */
    public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    private static final String SETTINGS_SAVE_FILE = "settings.romf";
    private static final String ICON_FILE = "clover.gif";
    private static final String CALCULATE = "Calculate";
    private static final String BACK = "Back";
    // frame object
    private final JFrame myFrame;
    private Settings mySettings;
    private final CenterPanel myCenterPanel;
    private final Menu myMenu;

    private GUI() {
        myFrame = new JFrame();
        try {
            final FileInputStream fileIn = new FileInputStream(SETTINGS_SAVE_FILE);
            final ObjectInputStream in = new ObjectInputStream(fileIn);
            mySettings = (Settings) in.readObject();
            in.close();
            fileIn.close();
        } catch (Exception e) {
            mySettings = Settings.DEFAULT;
        }
        myCenterPanel = new CenterPanel(mySettings);
        myMenu = new Menu(myFrame, mySettings);
    }

    private void start() {
        myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        myFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent theEvent) {
                try {
                    final FileOutputStream fileOut = new FileOutputStream(SETTINGS_SAVE_FILE);
                    final ObjectOutputStream out = new ObjectOutputStream(fileOut);
                    out.writeObject(mySettings);
                    out.close();
                    fileOut.close();
                } catch (IOException e) {
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final GUI gui = new GUI();
                gui.start();
            }
        });
    }

    private class SwitchButtonListener implements ActionListener {

        private static final String SAVE_FILE = "last_results.txt";

        public void actionPerformed(final ActionEvent theEvent) {
            try {
                if (!mySettings.anAlgorithmSelected()) {
                    throw new NullPointerException();
                }
                final JButton button = (JButton) theEvent.getSource();
                if (button.getText().equals(CALCULATE)) {
                    final String results = Calculator.getResult(mySettings);
                    saveToFile(results);
                    myCenterPanel.setResultsText(results);
                    button.setText(BACK);
                    myMenu.setButtonsEnabled(false);
                } else {
                    button.setText(CALCULATE);
                    myMenu.setButtonsEnabled(true);
                }
                myCenterPanel.nextCard();
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(myFrame, "Database File Not Found!", "Error",
                                              JOptionPane.ERROR_MESSAGE);
            } catch (NullPointerException e) {
                JOptionPane.showMessageDialog(myFrame, "Select the parameters first", "Error",
                                              JOptionPane.ERROR_MESSAGE);
            }
        }

        private void saveToFile(final String theResults) {
            try {
                final PrintStream out = new PrintStream(SAVE_FILE);
                out.print(theResults);
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
