package arithmetic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import algebra.Variable;

/**
 * <p>An object representing a Power in the form {@code base ^ exponent}.</p>
 *
 * @author Ricky Qin
 */
public class Power extends Expression {

// <-------------------------------- Static Methods -------------------------------->

    /**
     * Attempts to form a Power Object using the provided base and exponent.
     * Calling this method is favoured over directly calling the constructor because it checks for
     * trivial cases, in which case a simplier expression can be returned.
     *
     * @param base      The base.
     * @param exponent  The exponent.
     * @return          A BigRational object for trivial cases. Otherwise, a Power object is returned.
     */
    public static Expression parsePower(Expression base, Expression exponent) {
        if(base instanceof BigRational && exponent instanceof BigRational) {
            if(((BigRational)base).signum() == 0) {
                if(((BigRational)exponent).signum() == 0)
                    throw new ArithmeticException("Power: Zero raised to the zeroth power");
                if(((BigRational)exponent).signum() == -1)
                    throw new ArithmeticException("Power: Zero raised to a negative power");
                return BigRational.ZERO;
            }
            if(((BigRational)exponent).signum() == 0) return BigRational.ONE;
        }
        if(base instanceof BigRational && ((BigRational)base).equals(BigRational.ONE))
            return BigRational.ONE;
        if(exponent instanceof BigRational && ((BigRational)exponent).equals(BigRational.ONE))
            return base;
        return new Power(base, exponent);
    }

    /**
     * Returns the String representation of a Power with the specified base and exponent.
     *
     * @param base      The base of the Power.
     * @param exponent  The exponent of the Power.
     * @return          The normal String representation of the Power.
     */
    public static String toPowerString(Expression base, Expression exponent) {
        StringBuilder str = new StringBuilder();

        // print the base
        boolean printBaseBrackets = true;
        if(exponent.equals(BigRational.ONE)) printBaseBrackets = false;
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

    /**
     * Returns the LaTeX String representation of a Power with the specified base and exponent.
     *
     * @param base      The base of the Power.
     * @param exponent  The exponent of the Power.
     * @return          The LaTeX String representation of the Power.
     */
    public static String toPowerLatexString(Expression base, Expression exponent) {
        StringBuilder str = new StringBuilder();

        // print the base
        boolean printBaseBrackets = true;
        if(exponent.equals(BigRational.ONE)) printBaseBrackets = false;
        if(base instanceof BigRational) {
            if(((BigRational)base).signum() >= 0 && ((BigRational)base).isInteger()) printBaseBrackets = false;
        }
        if(base instanceof Variable) printBaseBrackets = false;

        boolean printExponent = false;
        if(!(exponent instanceof BigRational) || !exponent.equals(BigRational.ONE)) printExponent = true;
        if(!printExponent) printBaseBrackets = false;

        if(printBaseBrackets) str.append(surroundInLatexBrackets(base.toLatexString()));
        else str.append(base.toLatexString());
        if(printExponent) {
            str.append('^');
            str.append(surroundInCurlyBrackets(exponent.toLatexString()));
        }
        return str.toString();
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
     * This constructor is package private and is made this way because some Power objects
     * need to be made by bypassing validations.
     *
     * @param base      The base.
     * @param exponent  The exponent.
     */
    private Power(Expression base, Expression exponent) {
        this.base = base;
        this.exponent = exponent;
    }

// <-------------------- Methods Overriden from java.lang.Object -------------------->

    /**
     * Compares this Power with the specified object for equality.
     *
     * @param o  The object to which this Power is to be compared.
     * @return   True if the object is a Power and whose base and exponent
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
     *
     * @return  The hash code of this Power, equal to {@code 10007*base + exponent}.
     */
    @Override
    public int hashCode() {
        return 10007*base.hashCode() + exponent.hashCode();
    }


    /**
     * Returns a string representation of this Power.
     *
     * @return  The normal String representation of this Power.
     */
    @Override
    public String toString() {
        return toPowerString(base, exponent);
    }

// <---------------------- Methods Overriden from Superclasses ---------------------->

    /**
     * Returns a LaTeX string representation of this Power.
     *
     * @return  The LaTeX String representation of this Power.
     */
    @Override
    public String toLatexString() {
        return toPowerLatexString(base, exponent);
    }

    /**
     * Returns the String representation of this Power in function form.
     *
     * @return  The function String representation of this Power, formatted as
     * {@code Power(base, exponent)}.
     */
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

    /**
     * Attempts to compute a numerical exact value for this Power, given the values to substitute.
     *
     * @param variableValues  The values to substitute into the variables.
     * @return                The result of evaluating this Power.
     * @throws                ArithmeticException If the value of a variable in this Power is missing.
     */
    @Override
    protected Expression internalEvaluate(HashMap<String, Expression> variableValues) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Attempts to reduce the complexity of this Power by manipulating it algebraically.
     *
     * @return  A simplified Power that is equivalent to this Power.
     */
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
     *
     * @return  The base.
     */
    public Expression getBase() {
        return base;
    }

    /**
     * Gets the exponent of this Power.
     *
     * @return  The exponent.
     */
    public Expression getExponent() {
        return exponent;
    }
}
