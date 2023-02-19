package arithmetic;

import java.util.HashMap;

/**
 * The base class for all objects that represent numerical values.
 *
 * @author  Ricky Qin
 */
public abstract class BigNumber extends Expression {

    /**
     * The mathematical constant {@code e}, aka {@code Euler's number}.
     */
    final public static BigNumber E = new BigNumber() {

        private int hash = 0x5d37ae33;
        private String name = "e";

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
            return name;
        }

        @Override
        public String toFunctionString() {

            return "\"" + name + "\"";
        }

        @Override
        protected Expression internalEvaluate(HashMap<String, Expression> variableValues) {
            return this;
        }

        @Override
        public Expression simplify() {
            return this;
        }};

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

        @Override
        public String toFunctionString() {

            return "\"" + name + "\"";
        }

        @Override
        protected Expression internalEvaluate(HashMap<String, Expression> variableValues) {
            return this;
        }

        @Override
        public Expression simplify() {
            return this;
        }};

    /**
     * A mathematical expression representing positive infinity.
     */
    // final public static BigNumber POSITIVE_INFINITY = new BigNumber() {};

    /**
     * A mathematical expression representing positive infinity.
     */
    // final public static BigNumber NEGATIVE_INFINITY = new BigNumber() {};
}