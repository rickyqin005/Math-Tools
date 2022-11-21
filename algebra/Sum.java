package algebra;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import utility.Pair;

/**
 * <p>An object representing a sum or difference of expressions.</p>
 * <p>Internally, each term is stored as if it was a Power. A factor that
 * is not a Power has an exponent of {@code BigRational.ONE} and a factor
 * with a negative exponent indicates division. Any factors who are numbers
 * are stored as part of the coefficient.</p>
 */
class Sum extends Expression implements Iterable<Map.Entry<Expression, BigRational>> {

// <------------------------------ Instance Variables ------------------------------>

    /**
     * The constant term of this Sum, a rational number, initialized to {@code BigRational.ZERO}.
     */
    private BigRational constant = BigRational.ZERO;

    /**
     * A {@code TreeMap} of terms in this Sum, where each term is mapped to its coefficient.
     * The default coefficient is {@code BigRational.ONE} and is never {@code BigRational.ZERO}.
     * Note that rational numbers are not a part of this Map and is instead part of the coefficient.
     */
    private TreeMap<Expression, BigRational> terms = new TreeMap<>(new SumTermsComparator());

// <--------------------------------- Constructors --------------------------------->

    /**
     * Constructs a Sum object with the provided terms and their sign.
     * @param terms The terms. Each term is represented by a pair where the
     * first value is the expression and the second value is the leading sign:
     * 1 or -1 if the leading sign is positive or negative.
     */
    public Sum(ArrayList<Pair<Expression, Integer>> terms) {
        for(Pair<Expression, Integer> term: terms) {
            if(term.first() instanceof BigRational) {
                if(term.second() == 1) constant = (BigRational)constant.add((BigRational)term.first());
                else constant = (BigRational)constant.subtract((BigRational)term.first());

            } else if(term.first() instanceof Product) {
                BigRational termCoefficient = ((Product)term.first()).getCoefficient();
                Product newTerm = new Product(BigRational.ONE, ((Product)term.first()).getTerms());
                this.terms.putIfAbsent(newTerm, BigRational.ZERO);
                if(term.second() == 1) this.terms.replace(newTerm, (BigRational)this.terms.get(newTerm).add(termCoefficient));
                else this.terms.replace(newTerm, (BigRational)this.terms.get(newTerm).subtract(termCoefficient));

            } else {
                ArrayList<Expression> termFactor = new ArrayList<>();
                termFactor.add(term.first());
                Product newTerm = new Product(termFactor, new ArrayList<>());
                this.terms.putIfAbsent(newTerm, BigRational.ZERO);
                if(term.second() == 1) this.terms.replace(newTerm, (BigRational)this.terms.get(newTerm).add(BigRational.ONE));
                else this.terms.replace(newTerm, (BigRational)this.terms.get(newTerm).subtract(BigRational.ONE));

            }
        }
    }

// <-------------------- Methods Overriden from java.lang.Object -------------------->

    /**
     * Compares this Sum with the specified object for equality.
     * @param o The object to which this Sum is to be compared.
     * @return True if the object is a Sum and whose contents are identical
     * to this Sum.
     */
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof Sum)) return false;
        return (constant.equals(((Sum)o).constant) && terms.equals(((Sum)o).terms));
    }

    /**
     * Returns the hash code for this Sum.
     * @return The hash code for this Sum.
     */
    @Override
    public int hashCode() {
        return constant.hashCode() ^ terms.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        // TODO
        boolean isFirstTerm = true;
        for(Map.Entry<Expression, BigRational> term: terms.entrySet()) {
            if(isFirstTerm) isFirstTerm = false;
            else {
                if(term.getValue().signum() >= 0) str.append('+');
            }
            str.append(term.getValue().toString());
            str.append(term.getKey().toString());
        }

        if(constant.signum() != 0) {
            if(terms.size() > 0 && constant.signum() == 1) str.append('+');
            str.append(constant.toString());
        }
        return str.toString();
    }

// <---------------------- Methods Implemented for Interfaces ---------------------->

    /**
     * Returns an iterator over the terms of this Sum.
     * Each item in the iterator is an {@code Map.Entry} where the key is
     * the term and the value is the coefficient of that term.
     */
    @Override
    public Iterator<Map.Entry<Expression, BigRational>> iterator() {
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
        return this;
    }
}

/**
 * <p>A comparator that compares Expressions to determine their ordering in
 * a Product object.</p>
 *
 * <p>Note: Two Expressions of the same type are ordered arbitrarily. That is,
 * they can be in any order.</p>
 */
class SumTermsComparator implements Comparator<Expression> {

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
        if(expression instanceof Power) return 1;
        if(expression instanceof Product) return 2;
        if(expression instanceof Variable) return 3;
        if(expression instanceof Sum) return 4;
        if(expression instanceof BigRational) return 5;
        return 0;
    }
}