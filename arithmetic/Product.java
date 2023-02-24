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
 * <p>An object representing a product or division of two or more expressions.</p>
 *
 * <p>Internally, the factors are stored in a TreeMap, where each expression is
 * mapped to its coefficient (an Expression object). Essentially, each term is stored as if it
 * was a Power. Notice that the exponent can be any Expression, which is different from a Sum.
 * A factor that is not a Power has an implied exponent of {@code BigRational.ONE}
 * A factor with a negative exponent indicates division.</p>
 *
 * <p>Any terms that are constant rational numbers are combined and stored as part of the
 * rational term. For instance, {@code -4/3} and {@code 5} is part of the rational term while
 * {@code 5^0.5} is not. Internally, the key is the product/quotient of all such terms and is
 * mapped to {@code BigRational.ONE}. There is only one such key with type {@code BigRational}
 * and is always the first key in the TreeMap so it can be easily accessed.
 * If the rational term does not exist, there is no such mapping and the reference will
 * point to {@code null}.</p>
 *
 * <p>An empty product will be interpreted as {@code BigRational.ONE}
 * and thus will return this value instead of a Product object.</p>
 *
 * @author Ricky Qin
 */
public class Product extends Expression implements Iterable<Map.Entry<Expression, Expression>> {

// <-------------------------------- Static Methods -------------------------------->

    /**
     * Attempts to form a Product object with the provided terms. Since the references to the
     * lists are not stored internally, they can be mutated without affecting this object instance.
     *
     * @param factors   The factors.
     * @param divisors  The divisors.
     * @return          A Product object if there is more than one term. Otherwise, the singular
     * term is returned.
     */
    public static Expression parseProduct(ArrayList<Expression> factors, ArrayList<Expression> divisors) {
        Product product = new Product(factors, divisors);
        Expression simplerForm = checkForSimplerForms(product);
        if(simplerForm == null) return product;
        else return simplerForm;
    }

    /**
     * Attempts to form a Product object with the provided terms. Since the references to the lists
     * are not stored internally, they can be mutated without affecting this object instance.
     *
     * @param factor   The factor.
     * @param divisor  The divisor.
     * @return         A Product object if there is more than one term. Otherwise, the singular
     * term is returned.
     */
    public static Expression parseProduct(Expression factor, Expression divisor) {
        ArrayList<Expression> factors = new ArrayList<>();
        ArrayList<Expression> divisors = new ArrayList<>();
        factors.add(factor);
        divisors.add(divisor);
        Product product = new Product(factors, divisors);
        Expression simplerForm = checkForSimplerForms(product);
        if(simplerForm == null) return product;
        else return simplerForm;
    }

    /**
     * Attempts to form a Product object with the provided terms. Since
     * the references to {@code terms} are not stored internally, they can be mutated
     * without affecting this object instance.
     *
     * @param terms  The terms of this product.
     * @return       A Product object if there is more than one term. Otherwise, the singular
     * term is returned.
     */
    public static Expression parseProduct(TreeMap<Expression, Expression> terms) {
        Product product = new Product(terms);
        Expression simplerForm = checkForSimplerForms(product);
        if(simplerForm == null) return product;
        else return simplerForm;
    }

    /**
     * Checks if the provided Product object can be expressed as other simpler objects.
     * @param product  The Product.
     * @return         The simpler object if it can be expressed as such. Otherwise, {@code null} is returned.
     */
    private static Expression checkForSimplerForms(Product product) {
        if(product.terms.size() == 0) return BigRational.ONE;// empty product
        if(product.terms.size() == 1) {
            return product.terms.firstKey().pow(product.terms.firstEntry().getValue());
        }
        if(product.getCoefficient().equals(BigRational.ZERO)) return BigRational.ZERO;

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
    public static class ProductTermsComparator implements Comparator<Expression> {

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
            if(expression instanceof BigRational) return 0;
            if(expression instanceof Variable) return 1;
            if(expression instanceof Product) return 2;
            if(expression instanceof Power) return 3;
            if(expression instanceof Sum) return 4;
            if(expression == null) return 6;
            return 5;
        }
    }

// <------------------------------ Instance Variables ------------------------------>

    /**
     * A {@code TreeMap} of factors in this Product, where each factor is mapped to its exponent.
     * The implicit exponent is {@code BigRational.ONE} and is never {@code BigRational.ZERO}.
     * There is only one BigRational factor (the coefficient) and is always the first entry in
     * the map. For this entry, the coefficient is the key and the value is {@code BigRational.ONE}
     */
    private TreeMap<Expression, Expression> terms = new TreeMap<>(new ProductTermsComparator());

// <--------------------------------- Constructors --------------------------------->

    /**
     * Constructs a Product object using the provided terms. Since the references to the lists
     * are not stored internally, they can be mutated without affecting this object instance.
     *
     * @param factors   The terms.
     * @param divisors  The divisors.
     */
    private Product(ArrayList<Expression> factors, ArrayList<Expression> divisors) {
        BigRational coefficient = BigRational.ONE;

        for(Expression factor: factors) {
            if(factor instanceof BigRational) {
                coefficient = (BigRational)coefficient.multiply(factor);
            } else addTerm(new Pair<>(factor, 1));
        }
        for(Expression divisor: divisors) {
            if(divisor instanceof BigRational) {
                coefficient = (BigRational)coefficient.divide(divisor);
            } else addTerm(new Pair<>(divisor, -1));
        }
        if(!coefficient.equals(BigRational.ONE)) this.terms.put(coefficient, BigRational.ONE);
    }

    /**
     * Constructs a Product object with the provided terms. Since the reference to {@code terms}
     * is not stored internally, it can be mutated without affecting this object instance.
     *
     * @param terms  The terms, where the keys are the terms and the values are its exponents.
     */
    private Product(TreeMap<Expression, Expression> terms) {
        this.terms = new TreeMap<>(terms);
    }

// <-------------------- Methods Overriden from java.lang.Object -------------------->

    /**
     * Compares this Product with the specified object for equality.
     *
     * @param o  The object to which this Product is to be compared.
     * @return   True if the object is a Product and whose terms are identical to this Product.
     */
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof Product)) return false;
        return terms.equals(((Product)o).terms);
    }

    /**
     * Returns the hash code for this Product, equal to the hashcode of its terms.
     *
     * @return  The hash code for this Product.
     */
    @Override
    public int hashCode() {
        return terms.hashCode();
    }


    /**
     * Returns a string representation of this Product.
     *
     * @return  The normal String representation of this Product.
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        // print coefficient
        BigRational coefficient = getCoefficient();
        if(coefficient.equals(BigRational.ONE));
        else if(coefficient.equals(BigRational.NEGATIVE_ONE)) str.append('-');
        else str.append(coefficient);

        // print terms
        for(Map.Entry<Expression, Expression> term: terms.entrySet()) {
            if(term.getKey().equals(coefficient)) continue;

            String termStr = Power.toPowerString(term.getKey(), term.getValue());
            if(term.getKey() instanceof Sum && term.getValue().equals(BigRational.ONE))
                termStr = surroundInBrackets(termStr);
            if(str.length() > 0 && Character.isDigit(str.charAt(str.length()-1)) && termStr.length() > 0
                && Character.isDigit(termStr.charAt(0))) str.append('*');
            str.append(termStr);
        }

        return str.toString();
    }

// <---------------------- Methods Implemented for Interfaces ---------------------->

    /**
     * Returns an iterator over the terms of this Product.
     * Each item in the iterator is an {@code Map.Entry} where the key is
     * the factor and the value is its exponent. The
     *
     * @return An iterator over the terms in this Product.
     */
    @Override
    public Iterator<Map.Entry<Expression, Expression>> iterator() {
        return terms.entrySet().iterator();
    }

// <---------------------- Methods Overriden from Superclasses ---------------------->

    /**
     * Returns the LaTeX String representation of this Product.
     *
     * @return  The LaTeX String representation of this Product. If there any terms with negative
     * exponents, the string representation will be in the form of a fraction and those terms will
     * be placed at the bottom of the fraction. If the coefficient is negative, the sign will be placed
     * right before the beginning of the fraction (if in fraction form). If the coefficient is not an
     * integer, it will be split with its numerator and denominator on the top and bottom halves of
     * the fraction respectively. If in fraction form, the {@code \dfrac} command will be used.
     */
    @Override
    public String toLatexString() {
        StringBuilder str = new StringBuilder();

        ArrayList<String> top = new ArrayList<>();
        ArrayList<String> bottom = new ArrayList<>();
        int numItemsTop = 0;
        int numItemsBottom = 0;

        BigRational coefficientNum = getCoefficient().getNumerator().abs();
        BigRational coefficientDen = getCoefficient().getDenominator();
        if(!coefficientNum.equals(BigRational.ONE)) {
            top.add(coefficientNum.toLatexString());
            numItemsTop++;
        }
        if(!coefficientDen.equals(BigRational.ONE)) {
            bottom.add(coefficientDen.toLatexString());
            numItemsBottom++;
        }

        for(Map.Entry<Expression, Expression> term: terms.entrySet()) {
            if(term.getKey().equals(getCoefficient())) continue;

            boolean putOnTop = true;
            if(term.getValue() instanceof BigRational &&
                ((BigRational)term.getValue()).signum() == -1) putOnTop = false;
            if(term.getValue() instanceof Product &&
                ((Product)term.getValue()).getCoefficient().signum() == -1) putOnTop = false;

            if(putOnTop) numItemsTop++;
            else numItemsBottom++;
        }

        for(Map.Entry<Expression, Expression> term: terms.entrySet()) {
            if(term.getKey().equals(getCoefficient())) continue;

            boolean putOnTop = true;
            if(term.getValue() instanceof BigRational &&
                ((BigRational)term.getValue()).signum() == -1) putOnTop = false;
            if(term.getValue() instanceof Product &&
                ((Product)term.getValue()).getCoefficient().signum() == -1) putOnTop = false;

            String termStr = "";
            if(putOnTop) termStr = Power.toPowerLatexString(term.getKey(), term.getValue());
            else termStr = Power.toPowerLatexString(term.getKey(), term.getValue().negate());
            if(term.getKey() instanceof Sum && (putOnTop ? numItemsTop : numItemsBottom) > 1)
                termStr = surroundInLatexBrackets(termStr);
            if(putOnTop) top.add(termStr);
            else bottom.add(termStr);
        }
        if(top.size() == 0) top.add(BigRational.ONE.toLatexString());

        if(getCoefficient().signum() == -1) str.append('-');
        if(bottom.size() > 0) str.append("\\dfrac{");

        for(String termStr: top) {
            if(str.length() > 0 && Character.isDigit(str.charAt(str.length()-1)) && termStr.length() > 0
                && Character.isDigit(termStr.charAt(0))) str.append("\\cdot");
            str.append(termStr);
        }
        if(bottom.size() > 0) {
            str.append("}{");
            for(String termStr: bottom) {
                if(str.length() > 0 && Character.isDigit(str.charAt(str.length()-1)) && termStr.length() > 0
                && Character.isDigit(termStr.charAt(0))) str.append("\\cdot");
                str.append(termStr);
            }
            str.append('}');
        }
        return str.toString();
    }

    /**
     * Returns the String representation of this Product in function form.
     *
     * @return  The function String representation of this Product in the format
     * <code>Product({term:exponent}, ...)</code>.
     */
    @Override
    public String toFunctionString() {
        StringBuilder str = new StringBuilder();
        str.append("Product(");
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

    /**
     * Attempts to compute a numerical exact value for this Product, given the values to substitute.
     *
     * @param variableValues  The values to substitute into the variables.
     * @return                The result of evaluating this Product.
     * @throws                ArithmeticException If the value of a variable in this Product is missing.
     */
    @Override
    protected Expression internalEvaluate(HashMap<String, Expression> variableValues) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Attempts to reduce the complexity of this Product by manipulating it algebraically.
     *
     * @return  A simplified Product that is equivalent to this Product.
     */
    @Override
    public Expression simplify() {
        // simplify individual factors and divisors
        TreeMap<Expression, Expression> newTerms = new TreeMap<>();
        for(Map.Entry<Expression, Expression> entry: terms.entrySet()) {
            Expression newKey = entry.getKey().simplify();
            Expression newValue = entry.getValue().simplify();
            Expression res = newKey.pow(newValue);

        }

        // TODO Auto-generated method stub
        return null;
    }

// <---------------------------------- Own Methods ---------------------------------->

    /**
     * Adds the specified term to this Product Object. This is a private helper method to
     * construct Product objects.
     *
     * @param term  The term to add. The first value of the Pair is the term and the second
     * value indicates whether the term is multiplied or divided if it is non-negative or negative.
     */
    private void addTerm(Pair<Expression, Integer> term) {
        Expression termBase;
        if(term.first() instanceof Power) termBase = ((Power)term.first()).getBase();
        else termBase = term.first();

        Expression termExponent = terms.get(termBase);
        if(termExponent == null) termExponent = BigRational.ZERO;

        if(term.first() instanceof Power) {
            if(term.second() >= 0) termExponent = termExponent.add(((Power)term.first()).getExponent());
            else termExponent = termExponent.subtract(((Power)term.first()).getExponent());
        } else {
            if(term.second() >= 0) termExponent = termExponent.add(BigRational.ONE);
            else termExponent = termExponent.subtract(BigRational.ONE);
        }

        if(termExponent.equals(BigRational.ZERO)) terms.remove(termBase);
        else terms.put(termBase, termExponent);

        // TODO add restriction if its division!
    }

    /**
     * Gets the coefficient of this Product.
     *
     * @return  The coefficient.
     */
    public BigRational getCoefficient() {
        Expression firstTerm = terms.firstKey();
        if(firstTerm instanceof BigRational) return (BigRational)firstTerm;
        else return BigRational.ONE;
    }

    /**
     * Gets the terms of this Product.
     *
     * @return  A copy of the terms.
     */
    public TreeMap<Expression, Expression> getTerms() {
        TreeMap<Expression, Expression> newMap = (TreeMap<Expression, Expression>)terms.clone();
        return newMap;
    }

}
