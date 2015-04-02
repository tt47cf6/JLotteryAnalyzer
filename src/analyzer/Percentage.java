package analyzer;

import java.math.BigDecimal;
import java.math.MathContext;

public final class Percentage implements Comparable<Percentage> {

    public static final Percentage ZERO = new Percentage(0, 0);
    
    private final int myNumerator;
    
    private final int myDenominator;
    
    public Percentage(final int theNum, final int theDem) {
        myNumerator = theNum;
        myDenominator = theDem;
    }
    
    @Override
    public boolean equals(final Object theOther) {
        boolean result = false;
        if (theOther.getClass().equals(this.getClass()) && theOther != null) {
            final Percentage other = (Percentage) theOther;
            result = myNumerator == other.myNumerator;
            result = result && myDenominator == other.myDenominator;
        }
        return result;
    }
    
    @Override
    public int hashCode() {
        final String temp = "" + myNumerator + myDenominator;
        return temp.hashCode() + 1;
    }
    
    public Percentage addOccurance() {
        return new Percentage(myNumerator + 1, myDenominator + 1);
    }
    
    public Percentage addNonOccurance() {
        return new Percentage(myNumerator, myDenominator + 1);
    }
    
    public BigDecimal doubleValue() {
        final BigDecimal num = new BigDecimal(myNumerator);
        final BigDecimal dem = new BigDecimal(myDenominator);
        final BigDecimal divided = num.divide(dem, MathContext.DECIMAL128);
        return divided;
    }

    @Override
    public int compareTo(final Percentage theOther) {
        return this.doubleValue().compareTo(theOther.doubleValue());
    }
    
    @Override
    public String toString() {
        return doubleValue().toString();
    }
    
}
