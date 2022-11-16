package algebra;

import java.util.ArrayList;
import java.util.HashMap;

import utility.Pair;

class Product extends Expression {
    private ArrayList<Expression> factors = new ArrayList<>();
    private ArrayList<Expression> divisors = new ArrayList<>();

    public Product(ArrayList<Expression> factors, ArrayList<Expression> divisors) {
        if(factors.size() + divisors.size() == 0) throw new ArithmeticException("Product: Must have at least one factor or divisor");
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
    protected Expression internalEvaluate(HashMap<String, Expression> variableValues) {

        // evaluate individual factors and divisors
        ArrayList<Expression> newFactors1 = new ArrayList<>();
        ArrayList<Expression> newDivisors1 = new ArrayList<>();
        for(Expression expression: factors) newFactors1.add(expression.internalEvaluate(variableValues));
        for(Expression expression: divisors) newDivisors1.add(expression.internalEvaluate(variableValues));

        // combine like factors/divisors
        ArrayList<Expression> newFactors2 = new ArrayList<>();
        ArrayList<Expression> newDivisors2 = new ArrayList<>();
        BigRational coefficient = BigRational.ONE;
        HashMap<String, Expression> variableTerms = new HashMap<>();
        for(Expression expression: newFactors1) {
            if(expression instanceof BigRational) coefficient = coefficient.multiply((BigRational)expression);
            else if(expression instanceof Variable) {
                variableTerms.putIfAbsent(((Variable)expression).getLabel(), new Sum());
                // TODO
                throw new RuntimeException("TODO");
            }
        }
        for(Expression expression: newDivisors1) {
            if(expression instanceof BigRational) coefficient = coefficient.divide((BigRational)expression);
            else if(expression instanceof Variable) {
                // TODO
                throw new RuntimeException("TODO");
            }
        }
        return new Product(newFactors2, newDivisors2);
    }
}
