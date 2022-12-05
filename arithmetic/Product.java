package arithmetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import algebra.Variable;

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
     * Attempts to form a Product object with the provided terms.
     * @param factors The factors.
     * @param divisors The divisors.
     * @return A Product object if there is more than one non-rational term. Otherwise, a
     * BigRational is returned.
     * Since the references to the lists are not stored internally, they can be mutated without
     * affecting this object instance.
     */
    public static Expression parseProduct(ArrayList<Expression> factors, ArrayList<Expression> divisors) {
        Product product = new Product(factors, divisors);
        Expression simplerForm = checkForSimplerForms(product);
        if(simplerForm == null) return product;
        else return simplerForm;
    }

    /**
     * Attempts to form a Product object with the provided coefficient and terms.
     * @param coefficient The coefficient.
     * @return A Product object if there is more than one non-rational term. Otherwise, a
     * BigRational is returned.
     * Since the references to {@code terms} are not stored internally, they can be mutated without
     * affecting this object instance.
     */
    public static Expression parseProduct(BigRational coefficient, SortedMap<Expression, Expression> terms) {
        Product product = new Product(coefficient, terms);
        Expression simplerForm = checkForSimplerForms(product);
        if(simplerForm == null) return product;
        else return simplerForm;
    }

    /**
     * Checks if the provided Product object can be expressed as other simpler objects.
     * @param product The Product.
     * @return The simpler object if it can be expressed as such. Otherwise, {@code null} is returned.
     */
    private static Expression checkForSimplerForms(Product product) {
        if(product.coefficient.equals(BigRational.ZERO)) return BigRational.ZERO;
        if(product.terms.size() == 0) return product.coefficient;
        if(product.terms.size() == 1 && product.coefficient.equals(BigRational.ONE)) {
            return product.terms.firstEntry().getKey().pow(product.terms.firstEntry().getValue());
        }
        if(product.terms.size() == 1 && product.terms.firstEntry().getKey() instanceof Product &&
            product.terms.firstEntry().getValue().equals(BigRational.ONE))
            return new Product((BigRational)product.coefficient.multiply(((Product)product.terms.firstEntry().getKey()).coefficient),
                ((Product)product.terms.firstEntry().getKey()).terms);
        return null;
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

    /**
     * Constructs a Product object using the provided terms.
     * @param factors The terms.
     * @param divisors The divisors.
     * Since the references to the lists are not stored internally, they can be mutated without
     * affecting this object instance.
     */
    private Product(ArrayList<Expression> factors, ArrayList<Expression> divisors) {
        for(Expression factor: factors) {
            if(factor instanceof BigRational) {
                coefficient = (BigRational)coefficient.multiply(factor);
            } else {
                Expression term;
                Expression termExponent;
                if(factor instanceof Power) term = ((Power)factor).getBase();
                else term = factor;
                termExponent = terms.get(term);
                if(termExponent == null) termExponent = BigRational.ZERO;
                if(factor instanceof Power) termExponent = termExponent.add(((Power)factor).getExponent());
                else termExponent = termExponent.add(BigRational.ONE);
                if(termExponent.equals(BigRational.ZERO)) terms.remove(term);
                else terms.put(term, termExponent);
            }
        }
        for(Expression divisor: divisors) {
            if(divisor instanceof BigRational) {
                coefficient = (BigRational)coefficient.divide(divisor);
            } else {
                Expression term;
                Expression termExponent;
                if(divisor instanceof Power) term = ((Power)divisor).getBase();
                else term = divisor;
                termExponent = terms.get(term);
                if(termExponent == null) termExponent = BigRational.ZERO;
                if(divisor instanceof Power) termExponent = termExponent.subtract(((Power)divisor).getExponent());
                else termExponent = termExponent.subtract(BigRational.ONE);
                if(termExponent.equals(BigRational.ZERO)) terms.remove(term);
                else terms.put(term, termExponent);
            }
        }
    }

    /**
     * Constructs a Product object with the provided coefficient and terms.
     * @param coefficient The coefficient.
     * @param terms The terms, where the keys are the factors and the values are the exponents.
     * Since the reference to {@code terms} is not stored internally, it can be mutated without
     * affecting this object instance.
     */
    private Product(BigRational coefficient, SortedMap<Expression, Expression> terms) {
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
        StringBuilder str = new StringBuilder();

        // print coefficient
        if(terms.size() == 0) str.append(coefficient.toString());
        else {
            if(coefficient.equals(BigRational.ONE));
            else if(coefficient.equals(BigRational.NEGATIVE_ONE)) str.append('-');
            else str.append(coefficient.toString());
        }

        // print terms
        for(Map.Entry<Expression, Expression> term: terms.entrySet()) {
            String termStr = new Power(term.getKey(), term.getValue()).toString();
            if((term.getKey() instanceof Sum || term.getKey() instanceof Product) &&
                term.getValue().equals(BigRational.ONE)) termStr = surroundInBrackets(termStr);
            if(str.length() > 0 && Character.isDigit(str.charAt(str.length()-1)) &&
                Character.isDigit(termStr.charAt(0))) str.append('*');
            else str.append(termStr);
        }

        return str.toString();
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
    public String toFunctionString() {
        StringBuilder str = new StringBuilder();
        str.append("Product(");
        str.append(coefficient.toFunctionString());
        str.append(", ");
        Iterator<Map.Entry<Expression, Expression>> it = iterator();
        while(it.hasNext()) {
            Map.Entry<Expression, Expression> term = it.next();
            str.append('{');
            str.append(term.getKey().toFunctionString());
            str.append(':');
            str.append(term.getValue().toFunctionString());
            str.append('}');
            if(it.hasNext()) str.append(", ");
        }
        str.append(')');
        return str.toString();
    }

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
 * <p>Variable objects are ordered by their {@code compareTo} method.</p>
 * <p>Otherwise, two Expressions of the same type are ordered by insertion order.</p>
 */
class ProductTermsComparator implements Comparator<Expression> {

    @Override
    public int compare(Expression o1, Expression o2) {
        if(typeNum(o1) == typeNum(o2)) {
            if(o1 instanceof Variable) return ((Variable)o1).compareTo((Variable)o2);
            if(o1.equals(o2)) return 0;
            return 1;
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
