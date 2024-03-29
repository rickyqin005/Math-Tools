package algebra;

import java.util.HashMap;

import arithmetic.Expression;

/**
 * <p>An immutable class representing a variable.</p>
 *
 * @author Ricky Qin
 */
public class Variable extends Expression implements Comparable<Variable> {

// <------------------------------ Instance Variables ------------------------------>

    /**
     * The name of this Variable.
     */
    private String name;

// <--------------------------------- Constructors --------------------------------->

    /**
     * Constructs a Variable object with the provided name.
     *
     * @param name  The name of this Variable.
     */
    public Variable(String name) {
        this.name = name;
    }

// <-------------------- Methods Overriden from java.lang.Object -------------------->

    /**
     * Compares this Variable with the specified object for equality.
     *
     * @param o  The object to which this Variable is to be compared.
     * @return   True if the object is a Variable and whose name is equal to this Variable.
     */
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof Variable)) return false;
        return name.equals(((Variable)o).name);
    }

    /**
     * Returns the hash code for this Variable.
     *
     * @return  The hash code of this Variable, equal to the hash code of its name.
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Returns a string representation of this Variable.
     *
     * @return  The name of this Variable.
     */
    @Override
    public String toString() {
        return name;
    }

// <---------------------- Methods Implemented for Interfaces ---------------------->

    /**
     * Compares two Variables based on the natural ordering of their names.
     *
     * @param  o Another Variable.
     */
    @Override
    public int compareTo(Variable o) {
        return name.compareTo(o.name);
    }

// <---------------------- Methods Overriden from Superclasses ---------------------->

    /**
     * Returns the LaTeX String representation of this Variable.
     *
     * @return  The name of this Variable.
     */
    @Override
    public String toLatexString() {
        return name;
    }

    /**
     * Returns the String representation of this Variable in function form.
     *
     * @return  The name of this Variable with quotation marks {@code ""} surrounding it.
     */
    @Override
    public String toFunctionString() {
        return "\"" + name + "\"";
    }

    /**
     * Attempts to compute a numerical exact value for this Variable, given the values to substitute.
     *
     * @param variableValues  The values to substitute into the variables.
     * @return                The variable value, if it exists.
     * @throws                ArithmeticException If the value of this variable is missing.
     */
    @Override
    protected Expression internalEvaluate(HashMap<String, Expression> variableValues) {
        Expression variableValue = variableValues.get(name);
        if(variableValue == null) throw new ArithmeticException("Variable: Undefined variable value");
        return variableValue;
    }

    /**
     * Attempts to reduce the complexity of this Variable by manipulating it algebraically.
     *
     * @return  Itself, since it's already simplified!
     */
    @Override
    public Expression simplify() {
        return this;
    }

// <---------------------------------- Own Methods ---------------------------------->

    /**
     * Gets the name of this Variable.
     * @return The name.
     */
    public String getName() {
        return name;
    }
}
