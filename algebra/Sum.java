package algebra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

import utility.Pair;

/**
 * <p>An internal class representing a sum of expressions.</p>
 */
class Sum extends Expression {
    /**
     * The list of terms in this Sum along with their leading sign:
     * 1 or -1 if the leading sign is positive or negative.
     */
    private ArrayList<Pair<Expression, Integer>> terms = new ArrayList<>();

    /**
     * Constructs a Sum object with the provided terms and their sign.
     * @param terms The terms.
     */
    public Sum(ArrayList<Pair<Expression, Integer>> terms) {
        this.terms.addAll(terms);
    }

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
        ListIterator<Pair<Expression, Integer>> it1 = terms.listIterator();
        ListIterator<Pair<Expression, Integer>> it2 = ((Sum)o).terms.listIterator();
        while(it1.hasNext() && it2.hasNext()) {
            Pair<Expression, Integer> term1 = it1.next();
            Pair<Expression, Integer> term2 = it2.next();
            if(term1.second() != term2.second()) return false;
            if(!term1.first().equals(term2.first())) return false;
        }
        return (!it1.hasNext() && !it2.hasNext());
    }

    /**
     * Returns the hash code for this Sum.
     * @return The hash code for this Sum.
     */
    @Override
    public int hashCode() {
        return terms.hashCode();
    }

    @Override
    public String toString() {
        if(terms.size() == 0) return "0";
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < terms.size(); i++) {
            Pair<Expression, Integer> term = terms.get(i);
            Expression expression = term.first();
            int signum = term.second();
            if(signum == -1) str.append('-');
            else if(i > 0) str.append('+');
            if(expression instanceof Sum || (expression instanceof BigRational && ((BigRational)expression).signum() == -1)) {
                str.append(surroundInBrackets(expression.toString()));
            } else str.append(expression.toString());
        }
        return str.toString();
    }

    @Override
    protected Expression internalEvaluate(HashMap<String, Expression> variableValues) {

        // expand terms (distribute negative sign if leading sign is negative)
        ArrayList<Pair<Expression, Integer>> newTerms = new ArrayList<>();
        for(Pair<Expression, Integer> term: terms) {
            Expression result = term.first().internalEvaluate(variableValues);
            if(result instanceof Sum) {
                for(Pair<Expression, Integer> resultTerm: ((Sum)result).terms) {
                    newTerms.add(new Pair<>(resultTerm.first(), term.second()*resultTerm.second()));
                }
            } else newTerms.add(new Pair<>(result, term.second()));
        }

        // collect like terms

        // ArrayList<Pair<Expression, Integer>> newTerms2 = new ArrayList<>();
        // BigRational constantTerm = BigRational.ZERO;
        // for(Pair<Expression, Integer> term: newTerms) {
        //     if(term.first instanceof BigRational) {
        //         constantTerm = constantTerm.add((BigRational)term.first);
        //     } else newTerms2.add(term);
        // }

        return new Sum(newTerms);
    }
}
