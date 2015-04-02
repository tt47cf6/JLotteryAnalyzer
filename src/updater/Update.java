
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

import view.Menu;
import lotto.Lottery;
import lotto.MegaMillions;
import lotto.PowerBall;
import lotto.WALotto;

public final class Update extends SwingWorker<Integer, String> {
    
    /** The screen size of the user's machine. Used for centering the window. */
    public static final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();

    private static final int COMPLETE = 100;

    private static final Lottery[] UPDATE_THESE = {new WALotto(), new MegaMillions(),
                    new PowerBall()};
    
    private final JFrame myFrame;

    private final JDialog myDialog;

    private final JLabel myLabel;

    private final JButton myButton;

    private boolean okayToExit;

    public Update(final JFrame theFrame) {
        myFrame = theFrame;
        myDialog = new JDialog(theFrame, "Update");
        myLabel = new JLabel();
        myButton = new JButton("Exit");
        setup();

    }

    private void setup() {
        final JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(myLabel);
        panel.add(myButton);
        myDialog.add(panel);
        myButton.setVisible(false);
        myDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        myDialog.pack();

    }

    private void setDialogText(final String theText) {
        myLabel.setText(theText);
        myButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent theEvent) {
                if (okayToExit) {
                    myDialog.dispose();
                }
            }
        });
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
        final int extraVerticalSpace = SCREEN_SIZE.height - myDialog.getHeight();
        final int extraHorizontalSpace = SCREEN_SIZE.width - myDialog.getWidth();
        myDialog.setLocation(extraHorizontalSpace / 2, extraVerticalSpace / 2);
    }

    @Override
    protected Integer doInBackground() throws Exception {
        publish("Setting up Connection");
        setProgress(0);
        final int[] years = getLastYear();
        try {
            for (int year = 0; year < years.length; year++) {
                for (int game = 0; game < UPDATE_THESE.length; game++) {
                    publish("Updating " + UPDATE_THESE[game] + " in " + years[year]);
                    final UpdateGame updater = new UpdateGame(UPDATE_THESE[game], years[year]);
                    updater.update();
                    setProgress(((game + 1) * COMPLETE) / UPDATE_THESE.length - 1);
                }
            }
            publish("Update Successful.");
            setProgress(COMPLETE);
        } catch (FileNotFoundException e) {
            publish("File Database Error!");
            setProgress(COMPLETE);
        } catch (IOException e) {
            publish("Internet Connection Error!");
            setProgress(COMPLETE);
        }
        return 0;
    }

    @Override
    protected void process(final List<String> theList) {
        for (String update : theList) {
            setDialogText(update);
        }
        if (getProgress() == COMPLETE) {
            okayToExit = true;
            myButton.setVisible(true);
            myDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        }
    }
    
    @Override 
    protected void done() {
        final Menu menu = (Menu) myFrame.getJMenuBar();
        menu.setButtonsEnabled(true);
    }

    private int[] getLastYear() {
        int max = Lottery.MIN_COMMON_YEAR;
        try {
            final Set<Integer> commonYears = getYearsFromGame(UPDATE_THESE[0]);
            for (int i = 1; i < UPDATE_THESE.length; i++) {
                commonYears.retainAll(getYearsFromGame(UPDATE_THESE[i]));
            }
            for (int year : commonYears) {
                if (year > max) {
                    max = year;
                }
            }
        } catch (FileNotFoundException e) {
            max = Lottery.MIN_COMMON_YEAR;
        }
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

    private int currentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    private Set<Integer> getYearsFromGame(final Lottery game) throws FileNotFoundException {
        final Set<Integer> result = new HashSet<Integer>();
        final Scanner in = new Scanner(new File(game.dataFile()));
        while (in.hasNextLine()) {
            final String line = in.nextLine();
            final Scanner lineScan = new Scanner(line);
            lineScan.next();
            lineScan.next();
            result.add(lineScan.nextInt());
        }
        return result;
    }
}
