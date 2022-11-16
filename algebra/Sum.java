package algebra;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import utility.Pair;

class Sum extends Expression {
    private ArrayList<Pair<Expression, Integer>> terms = new ArrayList<>();

    public Sum(ArrayList<Pair<Expression, Integer>> terms) {
        this.terms.addAll(terms);
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

    protected Sum add(Sum other, int signum) {
        signum = (signum >= 0 ? 1 : -1);
        ArrayList<Pair<Expression, Integer>> newTerms = new ArrayList<>();
        Collections.copy(newTerms, terms);
        for(Pair<Expression, Integer> otherTerm: other.terms) {
            newTerms.add(new Pair<>(otherTerm.first(), signum*otherTerm.second()));
        }
        return new Sum(newTerms);
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
