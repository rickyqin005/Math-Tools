package algebra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

/**
 * <p>An internal class representing a product or division of expressions.</p>
 */
class Product extends Expression {
    /**
     * A list of factors in this Product.
     */
    private ArrayList<Expression> factors = new ArrayList<>();

    /**
     * A list of divisors in this Product.
     */
    private ArrayList<Expression> divisors = new ArrayList<>();

    /**
     * Constructs a Product Object with the provided factors and divisors.
     * @param factors A list of factors.
     * @param divisors A list of divisors.
     */
    public Product(ArrayList<Expression> factors, ArrayList<Expression> divisors) {
        if(factors.size() + divisors.size() == 0) throw new ArithmeticException("Product: Must have at least one factor or divisor");
        this.factors.addAll(factors);
        this.divisors.addAll(divisors);
    }

    /**
     * Compares this Product with the specified object for equality.
     * @param o The object to which this Product is to be compared.
     * @return True if the object is a Product and whose contents are
     * identical to this Product.
     */
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof Product)) return false;

        // compare factors
        ListIterator<Expression> it1 = factors.listIterator();
        ListIterator<Expression> it2 = ((Product)o).factors.listIterator();
        while(it1.hasNext() && it2.hasNext()) {
            Expression factor1 = it1.next();
            Expression factor2 = it2.next();
            if(!factor1.equals(factor2)) return false;
        }
        if(!(!it1.hasNext() && !it2.hasNext())) return false;

        // compare divisors
        it1 = divisors.listIterator();
        it2 = ((Product)o).divisors.listIterator();
        while(it1.hasNext() && it2.hasNext()) {
            Expression divisor1 = it1.next();
            Expression divisor2 = it2.next();
            if(!divisor1.equals(divisor2)) return false;
        }
        return (!it1.hasNext() && !it2.hasNext());
    }

    /**
     * Returns the hash code for this Product.
     * @return The hash code for this Product.
     */
    @Override
    public int hashCode() {
        return factors.hashCode() ^ divisors.hashCode();
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
                // variableTerms.putIfAbsent(((Variable)expression).getLabel(), new Sum());
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
