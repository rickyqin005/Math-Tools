package arithmetic;

import java.util.HashMap;

/**
 * <p>The base class for all objects that represent numerical values.</p>
 *
 * @author  Ricky Qin
 */
public abstract class BigNumber extends Expression {

// <------------------------------- Static Variables ------------------------------->

    /**
     * The mathematical constant {@code e}, aka {@code Euler's number}.
     */
    final public static BigNumber E = new BigNumber() {

        private int hash = 0x5d37ae33;

        private String name = "e";

        @Override
        public boolean equals(Object o) {
            // only instantiated once, so all references are equal
            return (this == o);
        }

        @Override
        public int hashCode() {
            return name.hashCode() ^ hash;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public String toLatexString() {
            return name;
        }

    };

    /**
     * The mathematical constant {@code pi}, the ratio of the circumference
     * of a circle to its diameter.
     */
    final public static BigNumber PI = new BigNumber() {

        private int hash = 0x180eae87;
        private String name = "pi";

        // only instantiated once, so all references are equal
        @Override
        public boolean equals(Object o) {
            return (this == o);
        }

        @Override
        public int hashCode() {
            return name.hashCode() ^ hash;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public String toLatexString() {
            return "\\" + name;
        }

    };

    /**
     * A mathematical expression representing positive infinity.
     */
    // final public static BigNumber POSITIVE_INFINITY = new BigNumber() {};

    /**
     * A mathematical expression representing positive infinity.
     */
    // final public static BigNumber NEGATIVE_INFINITY = new BigNumber() {};

// <---------------------- Methods Overriden from Superclasses ---------------------->

    /**
     * Returns the String representation of this BigNumber in function form.
     *
     * @return  The function String representation of this BigNumber, formatted the same
     * as the {@code toString()} method but with quotation marks {@code ""} surrounding it.
     */
    @Override
    public String toFunctionString() {
        return "\"" + toString() + "\"";
    }

    /**
     * Attempts to compute a numerical exact value for this BigNumber, given the values to substitute.
     *
     * @param variableValues  The values to substitute into the variables.
     * @return                Itself, since it's already a number!
     */
    @Override
    protected Expression internalEvaluate(HashMap<String, Expression> variableValues) {
        return this;
    }

    /**
     * Attempts to reduce the complexity of this BigNumber by manipulating it algebraically.
     *
     * @return  Itself, since its already simplified!
     */
    @Override
    public Expression simplify() {
        return this;
    };
}