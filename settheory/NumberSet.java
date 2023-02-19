package settheory;

import java.util.ArrayList;

import arithmetic.Expression;
import utility.Pair;

/**
 * <p>An object representing a set of numbers.</p>
 *
 * @author Ricky Qin
 */
public abstract class NumberSet {

// <------------------------------- Static Variables ------------------------------->

    final public static int NATURAL = 1;
    final public static int INTEGER = 2;
    final public static int RATIONAL = 3;
    final public static int REAL = 4;

// <------------------------------ Instance Variables ------------------------------>

    /**
     * The type of values stored in this NumberSet, one of {@code NATURAL}, {@code INTEGER},
     * {@code RATIONAL} and {@code REAL}.
     */
    private int type;

    /**
     * The values that are either include/exclude, depending on {@code isInverted}.
     */
    private ArrayList<Pair<Expression, Boolean>> values = new ArrayList<>();

// <--------------------------------- Constructors --------------------------------->

    /**
     * Constructs a NumberSet object covering the set of Real Numbers.
     */
    public NumberSet() {
        type = REAL;
    }

    /**
     * Constructs a NumberSet object covering the specified number set.
     *
     * @param type The type of number set.
     */
    public NumberSet(int type) {
        this.type = type;
    }

    /**
     * Constructs a NumberSet object that contains the specified values.
     *
     * @param type      The type of number set.
     * @param values    The values that are contained in this number set.
     */
    public NumberSet(int type, ArrayList<Expression> values) {
        this.type = type;
        for(Expression expression: values) {
            this.values.add(new Pair<>(expression, true));
        }
    }


    /**
     * @param set
     * @return NumberSet
     */
    public NumberSet intersection(NumberSet set) {
        // TODO Auto-generated method stub
        return null;
    }


    /**
     * @param set
     * @return NumberSet
     */
    public NumberSet union(NumberSet set) {
        // TODO Auto-generated method stub
        return null;
    }
}
