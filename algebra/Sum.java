package algebra;

import java.util.ArrayList;

import utility.Pair;

class Sum extends Expression {
    private ArrayList<Pair<Expression, Integer>> terms = new ArrayList<>();

    public Sum() {}

    public Sum add(Expression expression) {
        terms.add(new Pair<>(expression, 1));
        return this;
    }

    public Sum add(Expression expression, int signum) {
        terms.add(new Pair<>(expression, signum));
        return this;
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
                str.append('(');
                str.append(expression.toString());
                str.append(')');
            } else str.append(expression.toString());
        }
        return str.toString();
    }
}