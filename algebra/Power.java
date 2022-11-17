package algebra;

import java.util.HashMap;

/**
 * <p>An internal class representing a Power.</p>
 */
class Power extends Expression {
    /**
     * The base of this Power.
     */
    private Expression base;

    /**
     * The exponent of this Power.
     */
    private Expression exponent;

    /**
     * Constructs a Power object with the provided base and exponent.
     * @param base The base.
     * @param exponent The exponent.
     */
    public Power(Expression base, Expression exponent) {
        this.base = base;
        this.exponent = exponent;
    }

    /**
     * Compares this Power with the specified object for equality.
     * @param o The object to which this Power is to be compared.
     * @return True if the object is a Power and whose base and exponent
     * is equal to this Power.
     */
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof Power)) return false;
        return (base.equals(((Power)o).base) && exponent.equals(((Power)o).exponent));
    }

    /**
     * Returns the hash code for this Power.
     * @return The hash code for this Power.
     */
    @Override
    public int hashCode() {
        return base.hashCode() ^ exponent.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        // print the base
        boolean baseOuterBrackets = true;
        if(base instanceof BigRational) {
            if(((BigRational)base).signum() >= 0 && ((BigRational)base).isInteger()) baseOuterBrackets = false;
        }
        if(base instanceof Variable) baseOuterBrackets = false;
        if(baseOuterBrackets) str.append(surroundInBrackets(base.toString()));
        else str.append(base.toString());

        str.append('^');

        // print the exponent
        boolean exponentOuterBrackets = true;
        if(exponent instanceof BigRational) {
            if(((BigRational)exponent).signum() >= 0 && ((BigRational)exponent).isInteger()) exponentOuterBrackets = false;
        }
        if(exponent instanceof Variable) exponentOuterBrackets = false;
        if(exponentOuterBrackets) str.append(surroundInBrackets(exponent.toString()));
        else str.append(exponent.toString());

        return str.toString();
    }

    @Override
    protected Expression internalEvaluate(HashMap<String, Expression> variableValues) {
        Expression newBase = base.internalEvaluate(variableValues);
        Expression newExponent = exponent.internalEvaluate(variableValues);
        if(newBase instanceof BigRational && newExponent instanceof BigRational) {
            return ((BigRational)newBase).pow((BigRational)newExponent);
        }
        return new Power(newBase, newExponent);
    }
}
