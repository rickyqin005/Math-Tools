package arithmetic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import algebra.Variable;
import utility.Pair;

/**
 * <p>An object representing a sum or difference of two or more expressions.</p>
 *
 * <p>Internally, the expressions are stored in a TreeMap, where each expression is
 * mapped to its coefficient (a rational number). Essentially, each term is stored as
 * if it was a Product. Notice that the coefficient must be a BigRational, which is
 * different from a Product.
 * A term that is not a Product has an implied coefficient of {@code BigRational.ONE}.
 * A term with a negative exponent indicates subtraction.</p>
 *
 * <p>Any terms that are constant rational numbers are stored as part of the rational
 * term. For instance, {@code -4/3} and {@code 5} is part of the rational term while
 * {@code 5^0.5} is not. Internally, the key is {@code BigRational.ONE} and is mapped
 * to the value of the rational term, if such a term exists. It is always the last
 * entry in the TreeMap so it can be easily accessed. If the rational term does not
 * exist, there is no such mapping.</p>
 *
 * <p>An empty sum will be interpreted as {@code BigRational.ZERO}
 * and thus will return this value instead of a Sum object.</p>
 *
 * @author Ricky Qin
 */
public class Sum extends Expression implements Iterable<Map.Entry<Expression, BigRational>> {

// <-------------------------------- Static Methods -------------------------------->

    /**
     * Attempts to form a Sum Object with the provided terms.
     *
     * @param terms  A list of terms. Each term is a pair where the first value is the expression
     * and second value represents addition or subtraction depending on whether it is positive or negative.
     * @return       A Sum object if there is more than one term. If there is one term,
     * the term itself along with its leading sign is returned.
     */
    public static Expression parseSum(ArrayList<Pair<Expression, Integer>> terms) {
        Sum sum = new Sum(terms);
        Expression simplerForm = checkForSimplerForms(sum);
        if(simplerForm == null) return sum;
        else return simplerForm;
    }

    /**
     * Checks if the provided Sum object can be expressed as other simpler expressions.
     *
     * @param sum  The Sum object.
     * @return     The simpler object if it can be expressed as such. Otherwise, {@code null} is returned.
     */
    private static Expression checkForSimplerForms(Sum sum) {
        if(sum.terms.size() == 0) return BigRational.ZERO;// empty sum
        if(sum.terms.size() == 1) {
            if(sum.terms.firstEntry().getKey().equals(BigRational.ONE)) return sum.terms.firstEntry().getValue();
            return sum.terms.firstEntry().getKey().multiply(sum.terms.firstEntry().getValue());
        }
        return null;
    }

    /**
     * <p>A comparator that compares Expressions to determine their ordering in
     * a Product object.</p>
     *
     * <p>Variable objects are ordered by their {@code compareTo} method.</p>
     *
     * <p>Otherwise, two Expressions of the same type are ordered by insertion order.</p>
     */
    public static class SumTermsComparator implements Comparator<Expression> {

        /**
         * Compares its two arguments for order. Returns a negative integer, zero, or a positive
         * integer as the first argument is less than, equal to, or greater than the second.
         *
         * @param o1  The first expression.
         * @param o2  The second expression.
         * @return    A negative integer, zero, or a positive integer as the first argument is
         * less than, equal to, or greater than the second.
         */
        @Override
        public int compare(Expression o1, Expression o2) {
            if(typeNum(o1) == typeNum(o2)) {
                if(o1 instanceof Variable) return ((Variable)o1).compareTo((Variable)o2);
                if(o1.equals(o2)) return 0;
                return 1;
            }
            return Integer.compare(typeNum(o1), typeNum(o2));
        }

        /**
         * Checks the type of the argument and assigns a non-negative integer. A smaller value
         * indicates that the provided argument would come before an Expression with
         * a larger value.
         *
         * @param expression  The argument whose type will be checked.
         * @return            A non-negative integer.
         */
        private int typeNum(Expression expression) {
            if(expression instanceof Power) return 1;
            if(expression instanceof Product) return 2;
            if(expression instanceof Variable) return 3;
            if(expression instanceof Sum) return 4;
            if(expression instanceof BigRational) return 5;
            if(expression == null) return 6;
            return 0;
        }
    }

// <------------------------------ Instance Variables ------------------------------>

    /**
     * A {@code TreeMap} of terms in this Sum, where each term is mapped to its coefficient.
     * The default coefficient is {@code BigRational.ONE} and is never {@code BigRational.ZERO}.
     * Note that rational numbers are not a part of this Map and is instead part of the constant term.
     * This particular term has a key of {@code BigRational.ONE} and a value of the rational constant.
     * It is also the last entry in the TreeMap.
     */
    private TreeMap<Expression, BigRational> terms = new TreeMap<>(new SumTermsComparator());

// <--------------------------------- Constructors --------------------------------->

    /**
     * Constructs a Sum object with the provided terms and their sign.
     *
     * @param terms  The terms. Each term is represented by a pair where the
     * first value is the expression and the second value is the leading sign:
     * 1 or -1 if the leading sign is positive or negative.
     */
    private Sum(ArrayList<Pair<Expression, Integer>> terms) {
        for(Pair<Expression, Integer> term: terms) addTerm(term);
    }

    /**
     * Constructs a Sum object by cloning an existing Sum.
     *
     * @param sum  The Sum object to clone.
     */
    private Sum(Sum sum) {
        terms = (TreeMap<Expression, BigRational>)sum.terms.clone();
    }

// <-------------------- Methods Overriden from java.lang.Object -------------------->

    /**
     * Compares this Sum with the specified object for equality.
     *
     * @param o  The object to which this Sum is to be compared.
     * @return   True if the object is a Sum and whose contents are identical
     * to this Sum.
     */
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof Sum)) return false;
        return (terms.equals(((Sum)o).terms));
    }

    /**
     * Returns the hash code for this Sum, equal to the hashes of its terms.
     *
     * @return  The hash code for this Sum.
     */
    @Override
    public int hashCode() {
        return terms.hashCode();
    }

    /**
     * Returns a string representation of this Sum.
     *
     * @return  The normal String representation of this Sum.
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        boolean isFirstTerm = true;
        for(Map.Entry<Expression, BigRational> term: terms.entrySet()) {
            // do not print plus sign for the first term
            if(isFirstTerm) isFirstTerm = false;
            else if(term.getValue().signum() >= 0) str.append('+');

            if(term.getKey().equals(BigRational.ONE)) {
                str.append(term.getValue().toString());
                break;// always the last entry
            }

            // coefficients of one are implicit and should not be displayed
            if(term.getValue().equals(BigRational.ONE));
            else if(term.getValue().equals(BigRational.NEGATIVE_ONE)) str.append('-');
            else str.append(term.getValue().toString());

            // print the term itself
            if(term.getKey() instanceof BigRational);
            else if(term.getKey() instanceof Sum) str.append(surroundInBrackets(term.getKey().toString()));
            else {
                String termStr = term.getKey().toString();
                // add '*' symbol if coefficient and term needs to be separated
                if(str.length() > 0 && Character.isDigit(str.charAt(str.length()-1)) && termStr.length() > 0
                    && Character.isDigit(termStr.charAt(0))) str.append('*');
                str.append(termStr);
            }
        }
        return str.toString();
    }

// <---------------------- Methods Implemented for Interfaces ---------------------->

    /**
     * Returns an iterator over the terms of this Sum.
     * Each item in the iterator is a {@code Map.Entry} where the key is
     * the term and the value is the coefficient of that term.
     *
     * @return  An iterator over the terms in this Sum.
     */
    @Override
    public Iterator<Map.Entry<Expression, BigRational>> iterator() {
        return terms.entrySet().iterator();
    }

// <---------------------- Methods Overriden from Superclasses ---------------------->

    /**
     * Returns a Sum whose value is {@code (this + expression)}.
     *
     * @param  expression  The value to be added to this Sum.
     * @return             {@code this + expression}
     */
    @Override
    public Expression add(Expression expression) {
        Sum newSum = new Sum(this);
        if(expression instanceof BigRational) {
            newSum.addTerm(new Pair<>(expression, 1));
        } else if(expression instanceof Sum) {
            Iterator<Map.Entry<Expression, BigRational>> it = ((Sum)expression).iterator();
            while(it.hasNext()) {
                newSum.addTerm(it.next());
            }
        } else {
            newSum.addTerm(new Pair<>(expression, 1));
        }
        return newSum;
    }

    /**
     * Returns the LaTeX String representation of this Sum.
     *
     * @return  The LaTeX String representation of this Sum.
     */
    @Override
    public String toLatexString() {
        StringBuilder str = new StringBuilder();

        boolean isFirstTerm = true;
        for(Map.Entry<Expression, BigRational> term: terms.entrySet()) {
            // do not print plus sign for the first term
            if(isFirstTerm) isFirstTerm = false;
            else if(term.getValue().signum() >= 0) str.append('+');

            if(term.getKey().equals(BigRational.ONE)) {
                str.append(term.getValue().toLatexString());
                break;// always the last entry
            }

            // coefficients of one are implicit and should not be displayed
            if(term.getValue().equals(BigRational.ONE));
            else if(term.getValue().equals(BigRational.NEGATIVE_ONE)) str.append('-');
            else str.append(term.getValue().toLatexString());

            // print term itself
            if(term.getKey() instanceof BigRational);
            else if(term.getKey() instanceof Sum) str.append(surroundInLatexBrackets(term.getKey().toLatexString()));
            else {
                String termStr = term.getKey().toLatexString();
                // add \cdot if coefficient and term needs to be separated
                if(str.length() > 0 && Character.isDigit(str.charAt(str.length()-1)) && termStr.length() > 0
                    && Character.isDigit(termStr.charAt(0))) str.append("\\cdot");
                str.append(termStr);
            }
        }
        return str.toString();
    }

    /**
     * Returns the String representation of this Sum in function form.
     *
     * @return  The function String representation of this Sum in the format
     * <code>Sum({term:coefficient}, ...)</code>.
     */
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
        str.append(')');
        return str.toString();
    }

    /**
     * Attempts to compute a numerical exact value for this Sum, given the values to substitute.
     *
     * @param variableValues  The values to substitute into the variables.
     * @return                The result of evaluating this Sum.
     * @throws                ArithmeticException If the value of a variable in this Sum is missing.
     */
    @Override
    protected Expression internalEvaluate(HashMap<String, Expression> variableValues) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Attempts to reduce the complexity of this Sum by manipulating it algebraically.
     *
     * @return  A simplified Sum that is equivalent to this Sum.
     */
    @Override
    public Expression simplify() {
        // TODO Auto-generated method stub
        return this;
    }

// <---------------------------------- Own Methods ---------------------------------->

    /**
     * Adds the specified term to this Sum Object. This is a private helper method to construct
     * Sum objects.
     *
     * @param term  The term to add. The first value of the Pair is the term and the second
     * value indicates whether the term is added or subtracted if it is non-negative or negative.
     */
    private void addTerm(Pair<Expression, Integer> term) {
        BigRational termCoefficient;
        Expression newTerm;
        if(term.first() instanceof BigRational) {
            termCoefficient = (BigRational)term.first();
            newTerm = BigRational.ONE;
        } else if(term.first() instanceof Product) {
            termCoefficient = ((Product)term.first()).getCoefficient();
            TreeMap<Expression, Expression> termsNoCoefficient = ((Product)term.first()).getTerms();
            termsNoCoefficient.remove(termCoefficient);
            newTerm = Product.parseProduct(termsNoCoefficient);
        } else {
            termCoefficient = BigRational.ONE;
            ArrayList<Expression> termFactor = new ArrayList<>();
            termFactor.add(term.first());
            newTerm = Product.parseProduct(termFactor, new ArrayList<>());
        }

        BigRational newTermCoefficient = terms.get(newTerm);
        if(newTermCoefficient == null) newTermCoefficient = BigRational.ZERO;
        if(term.second() >= 0) newTermCoefficient = (BigRational)newTermCoefficient.add(termCoefficient);
        else newTermCoefficient = (BigRational)newTermCoefficient.subtract(termCoefficient);

        if(newTermCoefficient.equals(BigRational.ZERO)) terms.remove(newTerm);
        else terms.put(newTerm, newTermCoefficient);
    }

    /**
     * Adds the specified term to this Sum Object. This is a private helper method to
     * construct Sum objects.
     *
     * @param term  The term to add.
     */
    private void addTerm(Map.Entry<Expression, BigRational> term) {
        BigRational newCoefficient = terms.get(term.getKey());
        if(newCoefficient == null) newCoefficient = BigRational.ZERO;
        newCoefficient = (BigRational)newCoefficient.add(term.getValue());

        if(newCoefficient.equals(BigRational.ZERO)) terms.remove(term.getKey());
        else terms.put(term.getKey(), newCoefficient);
    }

    /**
     * Gets the rational constant term of this Sum.
     *
     * @return  The rational constant term.
     */
    public BigRational getRationalConstant() {
        Expression lastTerm = terms.lastKey();
        if(lastTerm instanceof BigRational) return terms.lastEntry().getValue();
        else return BigRational.ZERO;
    }

}
