package algebra;

public class Variable extends Expression {
    private String label;

    public Variable(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    public boolean equals(Object other) {
        return this.label.equals(((Variable)other).label);
    }

    @Override
    public Expression evaluate() {
        throw new RuntimeException("Cannot evaluate a variable expression");
    }

}
