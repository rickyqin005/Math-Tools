package algebra;

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
        if(base instanceof BigRational) {
            if(((BigRational)base).signum() >= 0 && ((BigRational)base).isInteger()) str.append(base.toString());
            else str.append(super.surroundInBrackets(base.toString()));
        } else str.append(super.surroundInBrackets(base.toString()));
        str.append('^');
        if(exponent instanceof BigRational) {
            if(((BigRational)exponent).signum() >= 0 && ((BigRational)exponent).isInteger()) str.append(exponent.toString());
            else str.append(super.surroundInBrackets(exponent.toString()));
        } else str.append(super.surroundInBrackets(exponent.toString()));
        return str.toString();
    }
}
