package algebra;

import java.util.ArrayList;

import utility.Pair;

class Sum extends Expression {
    private ArrayList<Pair<Expression, Integer>> terms = new ArrayList<>();

    public Sum() {}

    public Sum(ArrayList<Pair<Expression, Integer>> terms) {
        this.terms.addAll(terms);
    }

    @Override
    public String toString() {
        if(terms.size() == 0) return "0";
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < terms.size(); i++) {
            Pair<Expression, Integer> term = terms.get(i);
            Expression expression = term.first;
            int signum = term.second;
            if(signum == -1) str.append('-');
            else if(i > 0) str.append('+');
            if(expression instanceof Sum || (expression instanceof BigRational && ((BigRational)expression).signum() == -1)) {
                str.append(surroundInBrackets(expression.toString()));
            } else str.append(expression.toString());
        }
        return str.toString();
    }

    @Override
    public Expression evaluate() {
        ArrayList<Pair<Expression, Integer>> newTerms = new ArrayList<>();
        Expression ans = new BigRational(0);
        for(Pair<Expression, Integer> term: terms) {
            Expression result = term.first.evaluate();
        }
        return this;
    }
}