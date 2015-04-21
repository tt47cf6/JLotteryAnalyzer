package lotto;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * This class implements all of the shared behavior between the Lottery classes.
 * 
 * @author Robert
 */
public abstract class AbstractLottery implements Lottery {
	/**
	 * The upper limit of the number range, inclusive.
	 */
	private final int myRange;
	/**
	 * The number of number balls in this game.
	 */
	private final int myNumberOfBalls;
	/**
	 * The database file of Draws for this game.
	 */
	private final String myDatabaseFile;

	/**
	 * A protected constructor that accepts the final feilds from the child
	 * class.
	 * 
	 * @param theRange
	 *            the range of number balls
	 * @param theNumberOfBalls
	 *            the number of number balls
	 * @param theFile
	 *            the database file
	 */
	protected AbstractLottery(final int theRange, final int theNumberOfBalls,
			final String theFile) {
		myRange = theRange;
		myNumberOfBalls = theNumberOfBalls;
		myDatabaseFile = theFile;
	}

	/** {@inheritDoc} */
	@Override
	public int getRange() {
		return myRange;
	}

	/** {@inheritDoc} */
	@Override
	public int getNumberOfBalls() {
		return myNumberOfBalls;
	}

	/** {@inheritDoc} */
	@Override
	public String dataFile() {
		return myDatabaseFile;
	}

	/** {@inheritDoc} */
	@Override
	public int dataFileLength() throws FileNotFoundException {
		final Scanner in = new Scanner(new File(myDatabaseFile));
		int count = 0;
		while (in.hasNextLine()) {
			count++;
			in.nextLine();
		}
		in.close();
		return count;
	}

}
