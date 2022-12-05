package algebra;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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

// <-------------------------------- Static Methods -------------------------------->

    /**
     * Attempts to form a Sum Object with the provided terms.
     * @param terms The terms.
     * @return A Sum object if there is more than one term. If there is one term,
     * the term itself along with its leading sign is returned.
     */
    public static Expression parseSum(ArrayList<Pair<Expression, Integer>> terms) {
        Sum sum = new Sum(terms);
        Expression simplerForm = checkForSimplerForms(sum);
        if(simplerForm == null) return sum;
        else return simplerForm;
    }

    /**
     * Attempts to form a Sum Object with the provided terms and constant.
     * @param terms The terms.
     * @param constant The constant.
     * @return A Sum object if there is more than one term or if there is one term and a
     * non-zero constant. If there is one term, the term itself along with its leading sign is returned.
     * Otherwise, the constant term itself is returned.
     */
    public static Expression parseSum(TreeMap<Expression, BigRational> terms, BigRational constant) {
        Sum sum = new Sum(terms, constant);
        Expression simplerForm = checkForSimplerForms(sum);
        if(simplerForm == null) return sum;
        else return simplerForm;
    }

    /**
     * Checks if the provided Sum object can be expressed as other simpler objects.
     * @param sum The Sum.
     * @return The simpler object if it can be expressed as such. Otherwise, {@code null} is returned.
     */
    private static Expression checkForSimplerForms(Sum sum) {
        if(sum.terms.size() == 0) return sum.constant;
        if(sum.terms.size() == 1 && sum.constant.equals(BigRational.ZERO)) {
            return sum.terms.firstEntry().getKey().multiply(sum.terms.firstEntry().getValue());
        }
        return null;
    }

// <------------------------------ Instance Variables ------------------------------>

    /**
     * The constant term of this Sum, a rational number, initialized to {@code BigRational.ZERO}.
     */
    private BigRational constant = BigRational.ZERO;

    /**
     * A {@code TreeMap} of terms in this Sum, where each term is mapped to its coefficient.
     * The default coefficient is {@code BigRational.ONE} and is never {@code BigRational.ZERO}.
     * Note that rational numbers are not a part of this Map and is instead part of the constant term.
     */
    private TreeMap<Expression, BigRational> terms = new TreeMap<>(new SumTermsComparator());

// <--------------------------------- Constructors --------------------------------->

    /**
     * Constructs a Sum object with the provided terms and their sign.
     * @param terms The terms. Each term is represented by a pair where the
     * first value is the expression and the second value is the leading sign:
     * 1 or -1 if the leading sign is positive or negative.
     */
    private Sum(ArrayList<Pair<Expression, Integer>> terms) {
        for(Pair<Expression, Integer> term: terms) addTerm(term);
    }

    /**
     * Constructs a Sum object with the provided terms and constant value.
     * @param terms The terms.
     * @param constant The constant.
     */
    private Sum(TreeMap<Expression, BigRational> terms, BigRational constant) {
        this.terms = new TreeMap<Expression, BigRational>(terms);
        this.constant = constant;
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

        boolean isFirstTerm = true;
        for(Map.Entry<Expression, BigRational> term: terms.entrySet()) {
            if(isFirstTerm) isFirstTerm = false;
            else if(term.getValue().signum() >= 0) str.append('+');

            if(term.getValue().equals(BigRational.ONE));
            else if(term.getValue().equals(BigRational.NEGATIVE_ONE)) str.append('-');
            else str.append(term.getValue().toString());
            if(term.getKey() instanceof Sum) str.append(surroundInBrackets(term.getKey().toString()));
            else str.append(term.getKey().toString());
        }

        if(!constant.equals(BigRational.ZERO)) {
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
    public Expression add(Expression expression) {
        Sum newSum;
        if(expression instanceof BigRational) {
            newSum = new Sum(terms, (BigRational)constant.add(expression));
        } else if(expression instanceof Sum) {
            newSum = new Sum(terms, constant);
            Iterator<Map.Entry<Expression, BigRational>> it = newSum.iterator();
            while(it.hasNext()) {
                newSum.addTerm(it.next());
            }
            newSum.constant = (BigRational)newSum.constant.add(((Sum)expression).constant);
        } else {
            newSum = new Sum(terms, constant);
            newSum.addTerm(new Pair<>(expression, 1));
            return newSum;
        }
        return newSum;
    }

    @Override
    public String toFunctionString() {
        StringBuilder str = new StringBuilder();
        str.append("Sum(");
        Iterator<Map.Entry<Expression, BigRational>> it = iterator();
        while(it.hasNext()) {
            Map.Entry<Expression, BigRational> term = it.next();
            str.append('{');
            str.append(term.getKey().toFunctionString());
            str.append(':');
            str.append(term.getValue().toFunctionString());
            str.append('}');
            if(it.hasNext()) str.append(", ");
        }
        str.append(", ");
        str.append(constant.toFunctionString());
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
        // TODO Auto-generated method stub
        return this;
    }

// <---------------------------------- Own Methods ---------------------------------->

    /**
     * Adds the specified term to this Sum Object. This is a private helper method to construct
     * Sum objects.
     * @param term The term to add.
     */
    private void addTerm(Pair<Expression, Integer> term) {
        if(term.first() instanceof BigRational) {
            if(term.second() == 1) constant = (BigRational)constant.add(term.first());
            else constant = (BigRational)constant.subtract(term.first());

        } else {
            BigRational termCoefficient;
            Expression newTerm;
            if(term.first() instanceof Product) {
                termCoefficient = ((Product)term.first()).getCoefficient();
                newTerm = Product.parseProduct(BigRational.ONE, ((Product)term.first()).getTerms());
            } else {
                termCoefficient = BigRational.ONE;
                ArrayList<Expression> termFactor = new ArrayList<>();
                termFactor.add(term.first());
                newTerm = Product.parseProduct(termFactor, new ArrayList<>());
            }
            BigRational newTermCoefficient = terms.get(newTerm);
            if(newTermCoefficient == null) newTermCoefficient = BigRational.ZERO;
            if(term.second() == 1) newTermCoefficient = (BigRational)newTermCoefficient.add(termCoefficient);
            else newTermCoefficient = (BigRational)newTermCoefficient.subtract(termCoefficient);
            if(newTermCoefficient.equals(BigRational.ZERO)) terms.remove(newTerm);
            else terms.put(newTerm, newTermCoefficient);
        }
    }

    /**
     * Adds the specified term to this Sum Object. This is a private helper method to construct
     * Sum objects. This term must not represent a {@code BigRational}.
     * @param term The term to add.
     */
    private void addTerm(Map.Entry<Expression, BigRational> term) {
        if(term.getKey() instanceof BigRational) throw new RuntimeException("Sum: Cannot add constant as a term");
        terms.putIfAbsent(term.getKey(), BigRational.ZERO);
        terms.replace(term.getKey(), (BigRational)terms.get(term.getKey()).add(term.getValue()));
    }
}

/**
 * <p>A comparator that compares Expressions to determine their ordering in
 * a Product object.</p>
 *
 * <p>Variable objects are ordered by their {@code compareTo} method.</p>
 * <p>Otherwise, two Expressions of the same type are ordered by insertion order.</p>
 */
class SumTermsComparator implements Comparator<Expression> {

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
        if(expression instanceof Power) return 1;
        if(expression instanceof Product) return 2;
        if(expression instanceof Variable) return 3;
        if(expression instanceof Sum) return 4;
        if(expression instanceof BigRational) return 5;
        return 0;
    }
}
