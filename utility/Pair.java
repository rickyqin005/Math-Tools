package utility;

/**
 * An immutable object containing two values of any type.
 */
public class Pair<A, B> {
    /**
     * The first element.
     */
    private A a;

    /**
     * The second element.
     */
    private B b;

    /**
     * Constructs a Pair object with the provided values.
     * @param first The first value.
     * @param second The second value.
     */
    public Pair(A first, B second) {
        this.a = first;
        this.b = second;
    }

    /**
     * Compares this Pair with the specified object for equality.
     * @param o The object to which this Pair is to be compared.
     * @return True if the object is a Pair and whose contents are
     * equal to this Pair.
     */
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof Pair)) return false;
        return (a.equals(((Pair<?, ?>)o).a) && b.equals(((Pair<?, ?>)o).b));
    }

    /**
     * Returns the hash code for this Pair.
     * @return The hash code for this Pair.
     */
    @Override
    public int hashCode() {
        return a.hashCode() ^ b.hashCode();
    }

    /**
     * Returns a {@code String} object representing the value of this Pair.
     */
    @Override
    public String toString() {
        return "{" + a.toString() + ", " + b.toString() + "}";
    }

    /**
     * Returns the first value in the Pair.
     * @return The first value.
     */
    public A first() {
        return a;
    }

    /**
     * Returns the second value in the Pair.
     * @return The secound value.
     */
    public B second() {
        return b;
    }
}
