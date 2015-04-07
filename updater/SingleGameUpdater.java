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
import analyzer.Draw;

// TODO multithread updating the three games all at the same time
/**
 * This class updates the database for a specific game and year. Each game and
 * year pair defines a specific URL that must be pulled from online and parsed
 * through. This class uses a simple web-scraping approach since I have not
 * found any reliable online database or API for this purpose. This class
 * scrapes all draws from a given year, reads in all the draws it already has,
 * puts them into a Set to eliminate duplicates, and then writes them out to a
 * file in a ordered manner where the most recent draw is first.
 * 
 * @author Robert
 */
public final class SingleGameUpdater {

	/**
	 * The year to update for.
	 */
	private final String myYear;

	/**
	 * The game to update for.
	 */
	private final Lottery myGame;

	/**
	 * Set the final fields and do nothing else.
	 * 
	 * @param game
	 *            the game to update for
	 * @param year
	 *            the year to update in
	 */
	public SingleGameUpdater(final Lottery game, final int year) {
		myGame = game;
		myYear = "" + year;
	}

	// TODO when moving away from reading the DB each time, implement it here
	// too
	// TODO Compress Database with Huffman's
	/**
	 * Update the database for the given year and game.
	 * 
	 * @throws IOException
	 *             if the database file cannot be found
	 */
	public void update() throws IOException {
		final BufferedReader in = openConnection(myGame.updateURL() + myYear);
		final Scanner scan = new Scanner(removeTags(in));
		final List<Draw> newDraws = getNewDraws(scan);
		final Set<String> allLines = getAllLines();
		for (Draw draw : newDraws) {
			allLines.add(draw.toString().toUpperCase());
		}
		outputToFile(allLines);
	}

	private void outputToFile(final Set<String> allLines)
			throws FileNotFoundException {
		final Set<String> sorted = new TreeSet<String>(
				new DrawStringComparator());
		sorted.addAll(allLines);
		final PrintStream out = new PrintStream(myGame.dataFile());
		for (String line : sorted) {
			out.println(line);
		}
		out.close();
	}

	private Set<String> getAllLines() throws FileNotFoundException {
		final Set<String> result = new HashSet<String>();
		final Scanner in = new Scanner(new File(myGame.dataFile()));
		while (in.hasNextLine()) {
			String line = in.nextLine();
			line = line.toUpperCase();
			line = line.replace(' ', '\t');
			line = line.replace("\t\t", "\t");
			result.add(line);
		}
		return result;
	}

	/**
	 * Scrape the draws from online. The scraping algorithm is hard coded to the
	 * format of WALotto.com. If the format ever changes, this method will need
	 * to be rewritten. This method works on a Scanner object that is reading
	 * out lines from the online webpage. Because of the complicated nature of
	 * web scraping, I will forego commenting the body of this method. If it
	 * stops working, it really will need to be rewritten.
	 * 
	 * @param scan
	 *            the input scanner of the online page
	 * @return a list of all draws in the given year and game
	 */
	private List<Draw> getNewDraws(final Scanner scan) {
		final List<Draw> result = new ArrayList<Draw>();
		while (scan.hasNextLine()) {
			final String line = scan.nextLine();
			if (line.length() >= 10 && line.endsWith(myYear)) {
				try {
					final String date = Draw.parseDate(line);
					scan.nextLine();
					final String numberLine = scan.nextLine().replace('-', ' ');
					final int[] numbers = parseNumbers(numberLine.substring(0,
							myGame.getNumberOfBalls() * 3 - 1));
					final List<String> jackpotLine = delimit("$",
							scan.nextLine());
					final boolean jackpot = jackpotLine.get(
							jackpotLine.size() - 1).length() > 2;
					String totalLine = scan.nextLine();
					while (!totalLine.contains("Totals")) {
						totalLine = scan.nextLine();
					}
					final List<String> totalList = delimit("$", totalLine);
					final long payout = parseCurrency(totalList.get(totalList
							.size() - 1));
					if (myGame.hasBonusBall()) {
						final int startIndex = numberLine.indexOf('=') + 1;
						final int endIndex = startIndex + 2;
						final int bonusBall = Integer.parseInt(numberLine
								.substring(startIndex, endIndex));
						result.add(new Draw(date, numbers, bonusBall, jackpot,
								payout, myGame.getRange(),
								((AbstractBonusBall) myGame)
										.getBonusBallRange()));
					} else {
						result.add(new Draw(date, numbers, jackpot, payout,
								myGame.getRange()));
					}
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * This method takes in an HTML formatted input, and returns a String where
	 * all of the tags have been removed. This is used for easily scraping
	 * through a web site, even with heavy formatting.
	 * 
	 * @param input
	 *            HTML code, presumably a website
	 * @return the input free of all tags
	 * @throws IOException
	 *             if the input cannot be read.
	 */
	private static String removeTags(final BufferedReader input)
			throws IOException {
		final StringBuilder builder = new StringBuilder();
		String inputLine;
		// while there is more input
		while ((inputLine = input.readLine()) != null) {
			// while there is a complete tag in this line
			final StringBuilder lineBuilder = new StringBuilder(inputLine);
			while (lineBuilder.indexOf("<") >= 0
					&& lineBuilder.indexOf(">") >= 0) {
				final int startIndex = lineBuilder.indexOf("<");
				final int endIndex = lineBuilder.indexOf(">");
				// chop out the tag
				lineBuilder.delete(startIndex, endIndex + 1);
			}
			final String plainLine = lineBuilder.toString().trim();
			if (plainLine.length() > 0) {
				builder.append(plainLine);
				builder.append('\n');
			}
		}
		return builder.toString();
	}

	/**
	 * Opens a connection to the given String url and returns of BufferedReader
	 * of that connection.
	 * 
	 * @param url
	 *            the url to open a connection to
	 * @return the BufferedReader of the connection
	 * @throws IOException
	 *             if the url connection cannot be opened
	 */
	private static BufferedReader openConnection(final String url)
			throws IOException {
		final URL address = new URL(url);
		final URLConnection conn = address.openConnection();
		return new BufferedReader(new InputStreamReader(conn.getInputStream()));
	}

	/**
	 * A helper method to the web scraping method above. This method also
	 * expects the format given by WALotto.com
	 * 
	 * @param text
	 *            the string to parse a dollar number from
	 * @return the dollar number
	 */
	private static long parseCurrency(final String text) {
		final StringBuilder builder = new StringBuilder(text);
		builder.delete(0, builder.indexOf("$") + 1);
		while (builder.indexOf(",") >= 0) {
			builder.deleteCharAt(builder.indexOf(","));
		}
		return Long.parseLong(builder.toString());
	}

	/**
	 * Given a space-delimited string of numbers, return an array of those
	 * numbers.
	 * 
	 * @param text
	 *            space-delimited string of numbers
	 * @return an array of those numbers
	 */
	private static int[] parseNumbers(final String text) {
		final String[] split = text.split(" ");
		final int[] result = new int[split.length];
		for (int i = 0; i < split.length; i++) {
			result[i] = Integer.parseInt(split[i]);
		}
		return result;
	}

	/**
	 * Given a text and a pattern, split the text on the pattern and 
	 * return a List of Strings of that split text. 
	 * 
	 * @param pattern the pattern to split on
	 * @param text the text to split
	 * @return a List of strings of the split text
	 */
	private static List<String> delimit(final String pattern, final String text) {
		final String[] split = text.split(pattern);
		final List<String> result = new ArrayList<String>();
		for (String element : split) {
			result.add(element);
		}
		return result;
	}
}
