package algebra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * <p>An object representing a Power in the form {@code base ^ exponent}.</p>
 */
class Power extends Expression {

// <-------------------------------- Static Methods -------------------------------->

    /**
     * Attempts to form a Power Object using the provided base and exponent.
     * @param base The base.
     * @param exponent The exponent.
     * @return A BigRational object if the power can be directly evaluated into a BigRational with
     * neither the numerator or denominator exceeding {@code POWER_EVALUATION_THRESHOLD}. Otherwise,
     * a Power object is returned.
     * Calling this method is favoured over directly calling the constructor because it checks for
     * trivial cases, in which case a simplier expression can be returned.
     */
    public static Expression parsePower(Expression base, Expression exponent) {
        if(exponent instanceof BigRational) {
            if(base instanceof BigRational) return base.pow(exponent);
            if(exponent.equals(BigRational.ONE)) return base;
            if(exponent.equals(BigRational.ZERO)) return BigRational.ONE;
        }
        if(base instanceof BigRational) {
            if(base.equals(BigRational.ONE)) return BigRational.ONE;
            if(base.equals(BigRational.ZERO)) return BigRational.ZERO;
        }
        return new Power(base, exponent);
    }

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
     * Note: This constructor is package private and is made this way because some Power objects
     * need to be made by bypassing validations.
     */
    Power(Expression base, Expression exponent) {
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
        boolean printBaseBrackets = true;
        if(base instanceof BigRational) {
            if(((BigRational)base).signum() >= 0 && ((BigRational)base).isInteger()) printBaseBrackets = false;
        }
        if(base instanceof Variable) printBaseBrackets = false;

        boolean printExponent = false;
        boolean printExponentBrackets = false;
        if(!(exponent instanceof BigRational) || !exponent.equals(BigRational.ONE)) {
            printExponent = true;
            printExponentBrackets = true;
            if(exponent instanceof BigRational) {
                if(((BigRational)exponent).isInteger()) printExponentBrackets = false;
            }
            if(exponent instanceof Variable) printExponentBrackets = false;
        }
        if(!printExponent) printBaseBrackets = false;

        if(printBaseBrackets) str.append(surroundInBrackets(base.toString()));
        else str.append(base.toString());
        if(printExponent) {
            str.append('^');
            if(printExponentBrackets) str.append(surroundInBrackets(exponent.toString()));
            else str.append(exponent.toString());
        }
        return str.toString();
    }

// <---------------------- Methods Overriden from super types ---------------------->

    @Override
    public String toFunctionString() {
        StringBuilder str = new StringBuilder();
        str.append("Power(");
        str.append(base.toFunctionString());
        str.append(", ");
        str.append(exponent.toFunctionString());
        str.append(")");
        return str.toString();
    }

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
            return newBase.pow(newExponent);

        } else if(newBase instanceof Product) {// Exponent Law: (abc)^x = a^x * b^x * c^x
            Iterator<Map.Entry<Expression, Expression>> newBaseIterator = ((Product)newBase).iterator();
            ArrayList<Expression> newBaseFactors = new ArrayList<>();
            while(newBaseIterator.hasNext()) {
                Map.Entry<Expression, Expression> newBaseTerm = newBaseIterator.next();
                newBaseFactors.add(new Power(newBaseTerm.getKey(), (newBaseTerm.getValue().multiply(newExponent)).simplify()));
            }
            return Product.parseProduct(newBaseFactors, new ArrayList<>());

        } else if(newBase instanceof Power) {// Exponent Law: (b^x)^y = b^(x*y)
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
