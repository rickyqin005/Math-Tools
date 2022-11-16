package algebra;

import java.util.ArrayList;
import java.util.HashMap;

import utility.Pair;

public class Variable extends Expression {
    private String label;

    public Variable(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    public boolean equals(Object other) {
        return this.label.equals(((Variable)other).label);
    }

    @Override
    protected Expression internalEvaluate(HashMap<String, Expression> variableValues) {
        Expression variableValue = variableValues.get(label);
        if(variableValue == null) throw new ArithmeticException("Variable: Undefined variable value");
        return variableValue;
    }
}
