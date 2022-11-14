package algebra;

import java.util.ArrayList;

class Product extends Expression {
    private ArrayList<Expression> factors = new ArrayList<>();
    private ArrayList<Expression> divisors = new ArrayList<>();

    public Product(Expression expression, int multiplyOrDivide) {
        this.add(expression, multiplyOrDivide);
    }

    /**
     * @param expression
     * @param multiplyOrDivide A non-negative value denotes multiplication and a negative value denotes division.
     * @return
     */
    public Product add(Expression expression, int multiplyOrDivide) {
        multiplyOrDivide = (multiplyOrDivide >= 0 ? 1 : -1);
        if(multiplyOrDivide == 1) factors.add(expression);
        else divisors.add(expression);
        return this;
    }

    @Override
    public String toString() {
        assert(factors.size()+divisors.size() > 0);
        StringBuilder str = new StringBuilder();
        if(factors.size() == 0) str.append('1');
        for(int i = 0; i < factors.size(); i++) {
            Expression factor = factors.get(i);
            str.append('(');
            str.append(factor.toString());
            str.append(')');
        }
        if(divisors.size() > 0) {
            str.append('/');
            str.append('(');
            for(int i = 0; i < divisors.size(); i++) {
                Expression divisor = divisors.get(i);
                str.append('(');
                str.append(divisor.toString());
                str.append(')');
            }
            str.append(')');
        }
        return str.toString();
    }
}
