package arithmetic;

import java.math.BigInteger;
import java.util.HashMap;

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
public class BigRational extends BigNumber {

// <------------------------------- Static Variables ------------------------------->

    /**
     * The BigRational constant one.
     */
    final public static BigRational ONE = new BigRational(1);

    /**
     * The BigRational constant two.
     */
    final public static BigRational TWO = new BigRational(2);

    /**
     * The BigRational constant ten.
     */
    final public static BigRational TEN = new BigRational(10);

    /**
     * The BigRational constant zero.
     */
    final public static BigRational ZERO = new BigRational(0);

    /**
     * The BigRational constant negative one.
     */
    final public static BigRational NEGATIVE_ONE = new BigRational(-1);

    /**
     * The largest BigInteger that is allowed in a numerator or denominator after a {@code pow} operation.
     */
    final private static BigInteger POWER_EVALUATION_THRESHOLD = new BigInteger("1000");

// <-------------------------------- Static Methods -------------------------------->

    /**
     * Parses the provided string argument as a BigRational.
     * The separator between the numerator and denominator must be the {@code '/'} character.
     * Both the numerator and denominator can be either an integer or a decimal number.
     * @param str The {@code String} to be parsed.
     * @return The BigRational represented by the string argument.
     */
    final public static BigRational parseNumber(String str) {
        String[] parts = str.split("/");
        if(parts.length > 2) throw new NumberFormatException("BigRational: Invalid format");
        int[] decimalPoints = new int[parts.length];
        int[] decimalPlaceShift = new int[parts.length];
        BigInteger[] partsNum = new BigInteger[parts.length];
        for(int i = 0; i < parts.length; i++) {
            decimalPoints[i] = parts[i].indexOf(".");
            if(decimalPoints[i] != -1) {
                if(parts[i].lastIndexOf(".") != decimalPoints[i]) throw new NumberFormatException("BigRational: Invalid format");
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

    /**
     * Creates a new {@code ArithmeticException}.
     * Meant to be called when the resultant BigRational has a denominator of {@code zero}.
     * @return A new {@code ArithmeticException} instance.
     */
    private static ArithmeticException divisionByZeroError() {
        return new ArithmeticException("BigRational: Division by zero");
    }

// <------------------------------ Instance Variables ------------------------------>

    /**
     * The numerator of this BigRational.
     */
    private BigInteger numerator;

    /**
     * The denominator of this BigRational.
     */
    private BigInteger denominator;

// <--------------------------------- Constructors --------------------------------->

    /**
     * Constructs a BigRational with the specified integer value.
     * @param numerator The value to be represented.
     */
    public BigRational(int numerator) {
        this.numerator = new BigInteger(Integer.toString(numerator));
        this.denominator = BigInteger.ONE;
    }

    /**
     * Constructs a BigRational with the specified BigInteger value.
     * @param numerator The value to be represented.
     */
    public BigRational(BigInteger numerator) {
        this.numerator = numerator;
        this.denominator = BigInteger.ONE;
    }

    /**
     * Constructs a BigRational with the specified numerator and denominator.
     * @param numerator The value of the numerator.
     * @param denominator The value of the denominator.
     */
    public BigRational(BigInteger numerator, BigInteger denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
        this.normalize();
    }

// <-------------------- Methods Overriden from java.lang.Object -------------------->

    /**
     * Compares this BigRational with the specified object for equality.
     * @param o The object to which this BigInteger is to be compared.
     * @return True if the object is a BigRational and whose value is numerically
     * equal to this BigRational.
     */
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof BigRational)) return false;
        return (numerator.equals(((BigRational)o).numerator) &&
            denominator.equals(((BigRational)o).denominator));
    }

    /**
     * Returns the hash code for this BigRational.
     * @return The hash code for this BigRational.
     */
    @Override
    public int hashCode() {
        return numerator.hashCode() ^ denominator.hashCode();
    }

    /**
     * Returns a {@code String} object representing the value of this BigRational.
     */
    @Override
    public String toString() {
        if(isInteger()) return numerator.toString();
        return numerator.toString() + "/" + denominator.toString();
    }

// <---------------------- Methods Overriden from super types ---------------------->

    /**
     * Returns an Expression whose value is {@code (this + expression)}.
     *
     * @param expression The expression to be added to this BigRational.
     * @return {@code this + expression} An expression or a BigRational if
     * {@code expression} is a BigRational.
     */
    @Override
    public Expression add(Expression expression) {
        if(!(expression instanceof BigRational)) return expression.add(this);
        BigRational val = (BigRational)expression;
        return new BigRational((numerator.multiply(val.denominator)).add(denominator.multiply(val.numerator)),
            denominator.multiply(val.denominator));
        // a/b + c/d = ad/bd + bc/bd = (ad + bc)/bd
    }

    /**
     * Returns a BigRational whose value is {@code (this / val)}.
     *
     * @param  val The value by which this BigRational is to be divided.
     * @return {@code this / val}
     * @throws ArithmeticException if {@code val} is zero.
     */
    @Override
    public Expression divide(Expression expression) {
        if(!(expression instanceof BigRational)) return expression.reciprocal().multiply(this);
        BigRational val = (BigRational)expression;
        return new BigRational(numerator.multiply(val.denominator), denominator.multiply(val.numerator));
        // (a/b) / (c/d) = a/b * d/c = ad/bc
    }

    /**
     * Returns a BigRational whose value is {@code (this * val)}.
     *
     * @param  val The value to be multiplied by this BigRational.
     * @return {@code this * val}
     */
    @Override
    public Expression multiply(Expression expression) {
        if(!(expression instanceof BigRational)) return expression.multiply(this);
        BigRational val = (BigRational)expression;
        return new BigRational(numerator.multiply(val.numerator), denominator.multiply(val.denominator));
        // a/b * c/d = ac/bd
    }

    /**
     * Returns a BigRational whose value is {@code (-this)}.
     *
     * @return {@code -this}
     */
    @Override
    public BigRational negate() {
        return new BigRational(numerator.negate(), denominator);
    }

    /**
     * Returns a BigRational whose value is {@code (1 / this)}.
     * @return {@code 1 / this}.
     */
    @Override
    public BigRational reciprocal() {
        return new BigRational(denominator, numerator);
    }

    /**
     * Returns an Expression whose value is {@code (this - expression)}.
     *
     * @param expression The expression to be subtracted from this BigRational.
     * @return {@code this - expression} An expression or a BigRational if
     * {@code expression} is a BigRational.
     */
    @Override
    public Expression subtract(Expression expression) {
        if(!(expression instanceof BigRational)) return expression.negate().add(this);
        BigRational val = (BigRational)expression;
        return new BigRational((numerator.multiply(val.denominator)).subtract(denominator.multiply(val.numerator)),
            denominator.multiply(val.denominator));
        // a/b - c/d = ad/bd - bc/bd = (ad - bc)/bd
    }

    /**
     * Returns the LaTeX String representation of this BigRational.
     * @return A string.
     * If this BigRational is negative, the sign will be displayed directly in front. If the BigRational is
     * not an integer, it will be represented as a fraction using the {@code \dfrac} command.
     */
    @Override
    public String toLatexString() {
        if(isInteger()) return numerator.toString();
        StringBuilder str = new StringBuilder();
        if(signum() == -1) str.append('-');
        str.append("\\dfrac{");
        if(signum() == -1) str.append(numerator.abs().toString());
        else str.append(numerator.toString());
        str.append("}{");
        str.append(denominator.toString());
        str.append('}');
        return str.toString();
    }

    @Override
    public String toFunctionString() {
        return "\"" + toString() + "\"";
    }

    @Override
    protected Expression internalEvaluate(HashMap<String, Expression> variableValues) {
        return this;
    }

    @Override
    public Expression simplify() {
        return this;// already in lowest terms
    }

// <---------------------------------- Own Methods ---------------------------------->

    /**
     * Modifies the numerator and denominator such that they are in lowest terms and
     * that the denominator is strictly positive.
     * Note: the value represented by the BigRational does not change.
     */
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

    /**
     * Gets the numerator of this BigRational.
     * @return The numerator.
     */
    public BigRational getNumerator() {
        return new BigRational(numerator);
    }

    /**
     * Gets the denominator of this BigRational.
     * @return The denominator.
     */
    public BigRational getDenominator() {
        return new BigRational(denominator);
    }

    /**
     * Returns a BigRational whose value is the absolute value of this
     * BigRational.
     * @return {@code abs(this)}
     */
    public BigRational abs() {
        return new BigRational(numerator.abs(), denominator);
    }

    /**
     * Determines whether or not this BigRational is an integer.
     * @return True if this BigRational is an integer.
     */
    public boolean isInteger() {
        return denominator.equals(BigInteger.ONE);
    }

    /**
     * Determines whether or not this BigRational is a reciprocal of an integer.
     * @return True if this BigRational is a reciprocal of an integer.
     */
    public boolean isReciprocalInteger() {
        return numerator.abs().equals(BigInteger.ONE);
    }

    /**
     * Returns an int representing the sign of this BigRational:
     * {@code 1}, {@code 0}, or {@code -1} if this BigRational is positive, zero or negative.
     */
    public int signum() {
        return numerator.signum();
    }
}
