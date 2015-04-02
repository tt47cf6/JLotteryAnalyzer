
package controller;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import lotto.AbstractBonusBall;
import lotto.Lottery;
import analyzer.Algorithims;
import analyzer.GetDraws;

public final class Calculator {

    private Calculator() {
        // prevent instantiation of this class
    }

    public static String getResult(final Settings theSettings) throws FileNotFoundException {
        String result = "Internal Error in Calculator";
        if ((Boolean) theSettings.get(Settings.RAW_DATA)) {
            result = Algorithims.getRawData(theSettings.selectedGame(), createGetDraws(theSettings));
        } else {
            final Set<Integer> normalBallResults = processNormalGame(theSettings);
            final int ballsInGame = theSettings.selectedGame().getNumberOfBalls();
            if (theSettings.selectedGame().hasBonusBall()) {
                final Set<Integer> bonusBallResults = processBonusBallGame(theSettings);
                result = new Combinations(normalBallResults, bonusBallResults)
                                          .getOutput((Boolean) theSettings.get(Settings.WHEEL_RESULTS),
                                          ballsInGame);
            } else {
                result = new Combinations(normalBallResults)
                                          .getOutput((Boolean) theSettings.get(Settings.WHEEL_RESULTS),
                                           ballsInGame);
            }
        }
        return result;
    }

    private static Set<Integer> processNormalGame(Settings theSettings)
                    throws FileNotFoundException {
        final List<Integer> result = new LinkedList<Integer>((Set<Integer>) theSettings
                                                                .get(Settings.CUSTOM_NUMBERS));
        // remove too high of numbers from custom input
        removeNumbersHigherThan(result, theSettings.selectedGame().getRange());
        final GetDraws getDraws = createGetDraws(theSettings);
        if ((Boolean) theSettings.get(Settings.HOT_SELECTED)) {
            result.addAll(Algorithims.getHotNumbers(theSettings.selectedGame(), getDraws));
        }
        if ((Boolean) theSettings.get(Settings.COLD_SELECTED)) {
            result.addAll(Algorithims.getColdNumbers(theSettings.selectedGame(), getDraws));
        }
        if ((Boolean) theSettings.get(Settings.PERIODIC_SELECTED)) {
            result.addAll(Algorithims.getPeriodicNumbers(theSettings.selectedGame()));
        }
        if (!(Boolean) theSettings.get(Settings.INCLUSIVE)) {
            Collections.sort(result);
            for (int i = 0; i < result.size() - 1; i++) {
                if (result.get(i + 1) != result.get(i)) {
                    result.remove(i);
                    i--;
                }
            }
            if (!result.isEmpty()) {
                result.remove(result.size() - 1);
            }
        }
        return new TreeSet<Integer>(result);
    }

    private static Set<Integer> processBonusBallGame(Settings theSettings)
                    throws FileNotFoundException {
        final List<Integer> result =  new LinkedList<Integer>();
        final GetDraws getDraws = createGetDraws(theSettings);
        if ((Boolean) theSettings.get(Settings.HOT_SELECTED)) {
            result.addAll(Algorithims.getHotBonusBalls((AbstractBonusBall) theSettings
                            .selectedGame(), getDraws));
        }
        if ((Boolean) theSettings.get(Settings.COLD_SELECTED)) {
            result.addAll(Algorithims.getColdBonusBalls((AbstractBonusBall) theSettings
                            .selectedGame(), getDraws));
        }
        if ((Boolean) theSettings.get(Settings.PERIODIC_SELECTED)) {
            result.addAll(Algorithims.getPeriodicBonusBalls((AbstractBonusBall) theSettings
                            .selectedGame()));
        }
        return new TreeSet<Integer>(result);
    }
    
    // removes only numbers in custom numbers
    private static void removeNumbersHigherThan(final List<Integer> theList, final int max) {
        final Iterator<Integer> itr = theList.iterator();
        while (itr.hasNext()) {
            if (itr.next() > max) {
                itr.remove();
            }
        }
    }

    private static GetDraws createGetDraws(final Settings theSettings) {
        final Lottery game = theSettings.selectedGame();
        final int count = (Integer) theSettings.get(Settings.COUNT);
        final boolean jackpotOnly = (Boolean) theSettings.get(Settings.JACKPOT_ONLY);
        final long payout = (Long) theSettings.get(Settings.PAID_OVER_AMOUNT);
        return new GetDraws(game, count, jackpotOnly, payout);
    }
}
