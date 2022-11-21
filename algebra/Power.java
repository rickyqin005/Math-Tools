package algebra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>An object representing a Power in the form {@code base ^ exponent}.</p>
 */
class Power extends Expression {

// <------------------------------ Instance Variables ------------------------------>

    /**
     * The base of this Power.
     */
    private Expression base;

    /**
     * The exponent of this Power.
     */
    private Expression exponent;

// <--------------------------------- Constructors --------------------------------->

    /**
     * Constructs a Power object with the provided base and exponent.
     * @param base The base.
     * @param exponent The exponent.
     */
    public Power(Expression base, Expression exponent) {
        this.base = base;
        this.exponent = exponent;
    }

// <-------------------- Methods Overriden from java.lang.Object -------------------->

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

// <---------------------- Methods Overriden from super types ---------------------->

    @Override
    protected Expression internalEvaluate(HashMap<String, Expression> variableValues) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Expression simplify() {
        Expression newBase = base.simplify();
        Expression newExponent = exponent.simplify();
        if(newBase instanceof BigRational && newExponent instanceof BigRational) {
            return ((BigRational)newBase).pow((BigRational)newExponent);

        } else if(newBase instanceof Product) {// Exponent Law: (abc)^x = a^x * b^x * c^x
            Iterator<Map.Entry<Expression, Expression>> newBaseIterator = ((Product)newBase).iterator();
            ArrayList<Expression> newBaseFactors = new ArrayList<>();
            while(newBaseIterator.hasNext()) {
                Map.Entry<Expression, Expression> newBaseTerm = newBaseIterator.next();
                newBaseFactors.add(new Power(newBaseTerm.getKey(), (newBaseTerm.getValue().multiply(newExponent)).simplify()));
            }
            return new Product(newBaseFactors, new ArrayList<>());

        } else if(newBase instanceof Power) {// Exponent Law: (a^x)^y = a^(x*y)
            return new Power(((Power)newBase).base, ((Power)newBase).exponent.multiply(newExponent).simplify());

        }
        return new Power(newBase, newExponent);
    }

// <---------------------------------- Own Methods ---------------------------------->

    /**
     * Gets the base of this Power.
     * @return The base.
     */
    public Expression getBase() {
        return base;
    }

    /**
     * Gets the exponent of this Power.
     * @return The exponent.
     */
    public Expression getExponent() {
        return exponent;
    }
}
