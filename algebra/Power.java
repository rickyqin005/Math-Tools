package algebra;

import java.util.HashMap;

class Power extends Expression {

    private Expression base;
    private Expression exponent;

    public Power(Expression base, Expression exponent) {
        this.base = base;
        this.exponent = exponent;
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
