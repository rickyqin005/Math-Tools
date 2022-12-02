package algebra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * <p>An object representing a product or division of expressions.</p>
 *
 * <p>Internally, each factor is stored as if it was a Power. A factor that
 * is not a Power has an exponent of {@code BigRational.ONE} and a factor
 * with a negative exponent indicates division. Any factors who are numbers
 * are stored as part of the coefficient.</p>
 */
class Product extends Expression implements Iterable<Map.Entry<Expression, Expression>> {

// <-------------------------------- Static Methods -------------------------------->

    /**
     * Attempts to form a Product Object using the provided terms.
     * @param terms The terms.
     * @return A Product object if there is more than one non-rational term. Otherwise, a
     * BigRational is returned.
     * Since the references to the lists are not stored internally, they can be mutated without
     * affecting this object instance.
     */
    public static Expression parseProduct(ArrayList<Expression> factors, ArrayList<Expression> divisors) {
        Product res = new Product();
        for(Expression factor: factors) {
            if(factor instanceof BigRational) {
                res.coefficient = (BigRational)res.coefficient.multiply((BigRational)factor);
            } else {
                Expression term;
                Expression termExponent;
                if(factor instanceof Power) term = ((Power)factor).getBase();
                else term = factor;
                termExponent = res.terms.get(term);
                if(termExponent == null) termExponent = BigRational.ZERO;
                if(factor instanceof Power) termExponent = termExponent.add(((Power)factor).getExponent());
                else termExponent = termExponent.add(BigRational.ONE);
                if(termExponent.equals(BigRational.ZERO)) res.terms.remove(term);
                else res.terms.put(term, termExponent);
            }
        }
        for(Expression divisor: divisors) {
            if(divisor instanceof BigRational) {
                res.coefficient = (BigRational)res.coefficient.divide((BigRational)divisor);
            } else {
                Expression term;
                Expression termExponent;
                if(divisor instanceof Power) term = ((Power)divisor).getBase();
                else term = divisor;
                termExponent = res.terms.get(term);
                if(termExponent == null) termExponent = BigRational.ZERO;
                if(divisor instanceof Power) termExponent = termExponent.subtract(((Power)divisor).getExponent());
                else termExponent = termExponent.subtract(BigRational.ONE);
                if(termExponent.equals(BigRational.ZERO)) res.terms.remove(term);
                else res.terms.put(term, termExponent);
            }
        }
        if(res.terms.size() == 0) return res.coefficient;
        else return res;
    }

    public static String toProductString(Product product) {
        StringBuilder str = new StringBuilder();

        // print coefficient
        if(product.terms.size() == 0) str.append(product.coefficient.toString());
        else {
            if(product.coefficient.equals(BigRational.ONE));
            else if(product.coefficient.equals(BigRational.NEGATIVE_ONE)) str.append('-');
            else str.append(product.coefficient.toString());
        }

        // print terms
        for(Map.Entry<Expression, Expression> term: product.terms.entrySet()) {
            str.append(Power.toPowerString(new Power(term.getKey(), term.getValue())));
        }

        return str.toString();
    }

// <------------------------------ Instance Variables ------------------------------>

    /**
     * The coefficient of this Product, a rational number, initialized to {@code BigRational.ONE}.
     */
    private BigRational coefficient = BigRational.ONE;

    /**
     * A {@code TreeMap} of factors in this Product, where each factor is mapped to its exponent.
     * The default exponent is {@code BigRational.ONE} and is never {@code BigRational.ZERO}.
     * Note that rational numbers are not a part of this Map and is instead part of the coefficient.
     */
    private TreeMap<Expression, Expression> terms = new TreeMap<>(new ProductTermsComparator());

// <--------------------------------- Constructors --------------------------------->

    private Product() {}

    /**
     * Constructs a Product Object with the provided coefficient and terms.
     * @param coefficient The coefficient.
     * @param terms The terms, where the keys are the factors and the values are the exponents.
     * Since the reference to {@code terms} is not stored internally, it can be mutated without
     * affecting this object instance.
     * Note: this constructor is package private.
     */
    Product(BigRational coefficient, SortedMap<Expression, Expression> terms) {
        this.coefficient = coefficient;
        this.terms = new TreeMap<>(terms);
    }

// <-------------------- Methods Overriden from java.lang.Object -------------------->

    /**
     * Compares this Product with the specified object for equality.
     * @param o The object to which this Product is to be compared.
     * @return True if the object is a Product and whose terms are
     * identical to this Product.
     */
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof Product)) return false;
        return (coefficient.equals(((Product)o).coefficient) && terms.equals(((Product)o).terms));
    }

    /**
     * Returns the hash code for this Product.
     * @return The hash code for this Product.
     */
    @Override
    public int hashCode() {
        return coefficient.hashCode() ^ terms.hashCode();
    }

    @Override
    public String toString() {
        return toProductString(this);
    }

// <---------------------- Methods Implemented for Interfaces ---------------------->

    /**
     * Returns an iterator over the terms of this Product.
     * Each item in the iterator is an {@code Map.Entry} where the key is
     * the factor and the value is the exponent.
     */
    @Override
    public Iterator<Map.Entry<Expression, Expression>> iterator() {
        return terms.entrySet().iterator();
    }

// <---------------------- Methods Overriden from super types ---------------------->

    @Override
    protected Expression internalEvaluate(HashMap<String, Expression> variableValues) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Expression simplify() {
        // simplify individual factors and divisors
        TreeMap<Expression, Expression> newTerms = new TreeMap<>();
        for(Map.Entry<Expression, Expression> entry: terms.entrySet()) {
            Expression newKey = entry.getKey().simplify();
            Expression newValue = entry.getValue().simplify();
            Expression res = newKey.pow(newValue);

        }

        // TODO
        return null;
    }

// <---------------------------------- Own Methods ---------------------------------->

    /**
     * Gets the coefficient of this Product.
     * @return The coefficient.
     */
    public BigRational getCoefficient() {
        return coefficient;
    }

    /**
     * Gets the terms of this Product.
     * @return A read-only copy of the terms.
     */
    public SortedMap<Expression, Expression> getTerms() {
        return Collections.unmodifiableSortedMap(terms);
    }
}

/**
 * <p>A comparator that compares Expressions to determine their ordering in
 * a Product object.</p>
 *
 * <p>Note: Two Expressions of the same type are ordered arbitrarily. That is,
 * they can be in any order.</p>
 */
class ProductTermsComparator implements Comparator<Expression> {

    @Override
    public int compare(Expression o1, Expression o2) {
        if(typeNum(o1) == typeNum(o2)) {
            if(o1 instanceof Variable) return ((Variable)o1).compareTo((Variable)o2);
            if(o1.equals(o2)) return 0;
            return -1;
        }
        return Integer.compare(typeNum(o1), typeNum(o2));
    }

    private int typeNum(Expression expression) {
        if(expression instanceof BigRational) return 0;
        if(expression instanceof Variable) return 1;
        if(expression instanceof Product) return 2;
        if(expression instanceof Power) return 3;
        if(expression instanceof Sum) return 4;
        return 5;
    }
}
