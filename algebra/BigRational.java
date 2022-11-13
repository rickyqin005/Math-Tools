package algebra;

import java.math.BigInteger;

/**
 * <p>Immutable rational numbers represented in the form {@code P / Q}, where
 * {@code P} and {@code Q} are {@code BigInteger}s.</p>
 *
 * <p>After constructing or performing an operation on a BigRational, the fraction
 * {@code P / Q} will always be in lowest terms and {@code Q} will be <b>strictly</b>
 * positive. In the case that {@code P / Q} is equal to {@code 0}, the BigRational
 * will be represented as the fraction {@code 0 / 1}.</p>
 *
 * <p>BigRational constructors and operations throw {@code ArithmeticException} when
 * the denominator of the result is equal to {@code 0}.</p>
 */
public class BigRational extends Expression {
    final public static char DECIMAL_POINT = '.';

    final public static BigRational parseNumber(String str) {
        String[] parts = str.split("/");
        if(parts.length > 2) throw new RuntimeException("BigRational: Invalid format");
        int[] decimalPoints = new int[parts.length];
        int[] decimalPlaceShift = new int[parts.length];
        BigInteger[] partsNum = new BigInteger[parts.length];
        for(int i = 0; i < parts.length; i++) {
            decimalPoints[i] = parts[i].indexOf(".");
            if(decimalPoints[i] != -1) {
                if(parts[i].lastIndexOf(".") != decimalPoints[i]) throw new RuntimeException("BigRational: Invalid format");
            } else decimalPoints[i] = parts[i].length()-1;
        }
        for(int i = 0; i < parts.length; i++) decimalPlaceShift[i] = parts[i].length()-1-decimalPoints[i];
        int maxDecimalPlaceShift = 0;
        for(int i = 0; i < parts.length; i++) maxDecimalPlaceShift = Math.max(maxDecimalPlaceShift, decimalPlaceShift[i]);
        for(int i = 0; i < parts.length; i++) {
            partsNum[i] = new BigInteger(parts[i].replace(".", ""))
                    .multiply(BigInteger.TEN.pow(maxDecimalPlaceShift-decimalPlaceShift[i]));
        }
        if(parts.length == 1) return new BigRational(partsNum[0], BigInteger.TEN.pow(decimalPlaceShift[0]));
        else return new BigRational(partsNum[0], partsNum[1]);
    }

    private static ArithmeticException divisionByZeroError() {
        return new ArithmeticException("BigRational: Division by zero");
    }
    private BigInteger numerator;
    private BigInteger denominator;

    public BigRational(int numerator) {
        this.numerator = new BigInteger(Integer.toString(numerator));
        this.denominator = new BigInteger("1");
    }

    public BigRational(String numerator) {
        this.numerator = new BigInteger(numerator);
        this.denominator = new BigInteger("1");
    }

    public BigRational(BigInteger numerator) {
        this.numerator = numerator;
        this.denominator = new BigInteger("1");
    }

    public BigRational(int numerator, int denominator) {
        this.numerator = new BigInteger(Integer.toString(numerator));
        this.denominator = new BigInteger(Integer.toString(denominator));
        this.normalize();
    }

    public BigRational(String numerator, String denominator) {
        this.numerator = new BigInteger(numerator);
        this.denominator = new BigInteger(denominator);
        this.normalize();
    }

    public BigRational(BigInteger numerator, BigInteger denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
        this.normalize();
    }

    private void normalize() {
        if(denominator.signum() == -1) {
            numerator = numerator.negate();
            denominator = denominator.negate();
        }
        if(denominator.equals(BigInteger.ZERO)) throw divisionByZeroError();
        BigInteger gcd = numerator.gcd(denominator);
        numerator = numerator.divide(gcd);
        denominator = denominator.divide(gcd);
    }

    @Override
    public String toString() {
        if(denominator.equals(BigInteger.ZERO)) return "undefined";
        if(denominator.equals(BigInteger.ONE)) return numerator.toString();
        return numerator.toString() + "/" + denominator.toString();
    }

    public BigRational abs() {
        return new BigRational(numerator.abs(), denominator);
    }

    public BigRational add(BigRational val) {
        return new BigRational((numerator.multiply(val.denominator)).add(denominator.multiply(val.numerator)), denominator.multiply(val.denominator));
    }

    public BigRational divide(BigRational val) {
        return new BigRational(numerator.multiply(val.denominator), denominator.multiply(val.numerator));
    }

    public BigRational multiply(BigRational val) {
        return new BigRational(numerator.multiply(val.numerator), denominator.multiply(val.denominator));
    }

    public BigRational negate() {
        return new BigRational(numerator.negate(), denominator);
    }

    public BigRational subtract(BigRational val) {
        return new BigRational((numerator.multiply(val.denominator)).subtract(denominator.multiply(val.numerator)), denominator.multiply(val.denominator));
    }
}
