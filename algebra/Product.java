package algebra;

import java.util.ArrayList;

class Product extends Expression {
    private ArrayList<Expression> factors = new ArrayList<>();
    private ArrayList<Expression> divisors = new ArrayList<>();

    public Product(ArrayList<Expression> factors, ArrayList<Expression> divisors) {
        if(factors.size() + divisors.size() == 0) throw new RuntimeException();
        this.factors.addAll(factors);
        this.divisors.addAll(divisors);
    }

    @Override
    public String toString() {
        assert(factors.size()+divisors.size() > 0);
        StringBuilder str = new StringBuilder();

        // print factors
        if(factors.size() == 0) str.append('1');
        for(int i = 0; i < factors.size(); i++) {
            Expression factor = factors.get(i);
            if(i > 0) str.append('*');
            boolean factorOuterBrackets = true;
            if(factor instanceof BigRational && ((BigRational)factor).isInteger()) factorOuterBrackets = false;
            if(factor instanceof Power) factorOuterBrackets = false;
            if(factor instanceof Variable) factorOuterBrackets = false;
            if(factorOuterBrackets) str.append(surroundInBrackets(factor.toString()));
            else str.append(factor.toString());
        }

        // print divisors
        if(divisors.size() > 0) {
            str.append('/');
            if(divisors.size() > 1) str.append('(');
            for(int i = 0; i < divisors.size(); i++) {
                Expression divisor = divisors.get(i);
                if(i > 0) str.append('*');
                boolean divisorOuterBrackets = true;
                if(divisor instanceof BigRational && ((BigRational)divisor).isInteger()) divisorOuterBrackets = false;
                if(divisor instanceof Power) divisorOuterBrackets = false;
                if(divisor instanceof Variable) divisorOuterBrackets = false;
                if(divisorOuterBrackets) str.append(surroundInBrackets(divisor.toString()));
                else str.append(divisor.toString());
            }
            if(divisors.size() > 1) str.append(')');
        }
        return str.toString();
    }

    @Override
    public Expression evaluate() {
        for(Expression factor: factors) factor.evaluate();
        for(Expression divisor: divisors) divisor.evaluate();

        for(int i = 0; i < factors.size(); i++) {

        }
        for(int i = 0; i < divisors.size(); i++) {

        }
        return this;
    }
}
