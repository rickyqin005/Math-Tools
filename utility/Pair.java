package utility;

/**
 * An immutable object containing two values of any type.
 */
public class Pair<A, B> {
    private A a;
    public B b;

    public Pair(A first, B second) {
        this.a = first;
        this.b = second;
    }

    public A first() {
        return a;
    }

    public B second() {
        return b;
    }
}
