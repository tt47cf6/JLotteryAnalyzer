
package updater;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import lotto.AbstractBonusBall;
import lotto.Lottery;
import util.DrawStringComparator;
import util.NumberUtils;
import analyzer.Draw;

/**
 * 
 * @author Robert
 */
public final class UpdateGame {

    private final String myYear;

    private final Lottery myGame;

    public UpdateGame(final Lottery theGame, final int year) {
        myGame = theGame;
        myYear = "" + year;
    }

    public void update() throws FileNotFoundException, IOException {
        final BufferedReader in = openConnection(myGame.updateURL() + myYear);
        final Scanner scan = new Scanner(removeTags(in));
        final List<Draw> newDraws = getNewDraws(scan);
        final Set<String> allLines = getAllLines();
        addNewDraws(allLines, newDraws);
        outputToFile(allLines);
    }

    private void outputToFile(final Set<String> allLines) throws FileNotFoundException {
        final PrintStream out = new PrintStream(myGame.dataFile());
        final Set<String> sorted = new TreeSet<String>(new DrawStringComparator());
        sorted.addAll(allLines);
        for (String line : sorted) {
            out.println(line);
        }
    }

    private void addNewDraws(final Set<String> allLines, final List<Draw> newDraws) {
        for (Draw draw : newDraws) {
            allLines.add(draw.toString().toUpperCase());
        }
    }

    private Set<String> getAllLines() throws FileNotFoundException {
        final Set<String> result = new HashSet<String>();
        final Scanner in = new Scanner(new File(myGame.dataFile()));
        while (in.hasNextLine()) {
            result.add(in.nextLine().toUpperCase());
        }
        return result;
    }

    private List<Draw> getNewDraws(final Scanner scan) {
        final List<Draw> result = new ArrayList<Draw>();
        while (scan.hasNextLine()) {
            final String line = scan.nextLine();
            if (line.length() >= 10 && line.endsWith(myYear)) {
                try {
                    final String date = Draw.parseDate(line);
                    scan.nextLine();
                    final String numberLine = scan.nextLine().replace('-', ' ');
                    final int[] numbers =
                                    NumberUtils.parseNumbers(numberLine.substring(0, myGame
                                                    .getNumberOfBalls() * 3 - 1));
                    final List<String> jackpotLine = NumberUtils.delimit('$', scan.nextLine());
                    final boolean jackpot =
                                    jackpotLine.get(jackpotLine.size() - 1).length() > 2;
                    String totalLine = scan.nextLine();
                    while (!totalLine.contains("Totals")) {
                        totalLine = scan.nextLine();
                    }
                    final List<String> totalList = NumberUtils.delimit('$', totalLine);
                    final long payout =
                                    NumberUtils.parseCurrency(totalList.get(totalList.size() - 1));
                    if (myGame.hasBonusBall()) {
                        final int startIndex = numberLine.indexOf('=') + 1;
                        final int endIndex = startIndex + 2;
                        final int bonusBall =
                                        Integer.parseInt(numberLine.substring(startIndex,
                                                                              endIndex));
                        result.add(new Draw(date, numbers, bonusBall, jackpot, payout, myGame
                                        .getRange(), ((AbstractBonusBall) myGame)
                                        .getBonusBallRange()));
                    } else {
                        result.add(new Draw(date, numbers, jackpot, payout, myGame.getRange()));
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private static String removeTags(final BufferedReader input) throws IOException {
        final StringBuilder builder = new StringBuilder();
        String inputLine;
        while ((inputLine = input.readLine()) != null) {
            while (inputLine.contains("<") && inputLine.contains(">")) {
                final int startIndex = inputLine.indexOf('<');
                final int endIndex = inputLine.indexOf('>');
                inputLine =
                                inputLine.substring(0, startIndex)
                                                + inputLine.substring(endIndex + 1);
            }
            inputLine = inputLine.trim();
            if (inputLine.length() > 0) {
                builder.append(inputLine);
                builder.append('\n');
            }
        }
        return builder.toString();
    }

    private static BufferedReader openConnection(final String url) throws IOException {
        final URL update = new URL(url);
        final URLConnection conn = update.openConnection();
        return new BufferedReader(new InputStreamReader(conn.getInputStream()));
    }
}
