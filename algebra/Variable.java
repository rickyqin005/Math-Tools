package algebra;

import java.util.ArrayList;
import java.util.HashMap;

import utility.Pair;

/**
 * <p>An object representing a variable.</p>
 */
public class Variable extends Expression {
    /**
     * The name of this Variable.
     */
    private String name;

    /**
     * Constructs a Variable object with the provided name.
     * @param name The name.
     */
    public Variable(String name) {
        this.name = name;
    }

    /**
     * Compares this Variable with the specified object for equality.
     * @param o The object to which this Variable is to be compared.
     * @return True if the object is a Variable and whose name
     * is equal to this Variable.
     */
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof Variable)) return false;
        return name.equals(((Variable)o).name);
    }

    /**
     * Returns the hash code for this Variable.
     * @return The hash code for this Variable.
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    protected Expression internalEvaluate(HashMap<String, Expression> variableValues) {
        Expression variableValue = variableValues.get(name);
        if(variableValue == null) throw new ArithmeticException("Variable: Undefined variable value");
        return variableValue;
    }

    /**
     * Gets the name of this Variable.
     * @return The name.
     */
    public String getName() {
        return name;
    }
}
