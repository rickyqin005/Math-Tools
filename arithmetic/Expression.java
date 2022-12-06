package arithmetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import algebra.Variable;
import utility.Pair;

/**
 * <p>The base class for immutable objects representing mathematical expressions.</p>
 */
public abstract class Expression {

// <------------------------------- Static Variables ------------------------------->

    final private static int NUMBER_LITERAL = 1;
    final private static int OPEN_BRACKET = 2;
    final private static int CLOSE_BRACKET = 3;
    final private static int PLUS = 4;
    final private static int MINUS = 5;
    final private static int MULTIPLY = 6;
    final private static int DIVIDE = 7;
    final private static int CARET = 8;// exponentiation
    final private static int VARIABLE = 9;

// <-------------------------------- Static Methods -------------------------------->

    /**
     * Splits a {@code String} representation of an Expression into its tokens
     * @param str The {@code String}
     * @return An {@code ArrayList} of tokens and its token type
     */
    private static ArrayList<Pair<String, Integer>> tokenizeExpression(String str) {
        // Remove whitespace characters
        str = str.replaceAll("\\s", "");

        // Split string into tokens
        ArrayList<Pair<String, Integer>> tokens = new ArrayList<>();
        for(int i = 0; i < str.length();) {
            if(Character.isDigit(str.charAt(i)) || str.charAt(i) == '.') {// number literal
                int startIdx = i;
                int integerPartLen = 0;
                int decimalPartLen = 0;
                for(; i < str.length(); i++) {
                    if(!Character.isDigit(str.charAt(i))) break;
                    integerPartLen++;
                }
                if(i < str.length() && str.charAt(i) == '.') {
                    for(i++; i < str.length(); i++) {
                        if(!Character.isDigit(str.charAt(i))) break;
                        decimalPartLen++;
                    }
                }
                if(integerPartLen + decimalPartLen == 0) throw new NumberFormatException("Expression: Illegal character \".\"");

                tokens.add(new Pair<>(str.substring(startIdx, i), NUMBER_LITERAL));
            } else if(str.charAt(i) == '(') {
                tokens.add(new Pair<>("(", OPEN_BRACKET)); i++;
            } else if(str.charAt(i) == ')') {
                tokens.add(new Pair<>(")", CLOSE_BRACKET)); i++;
            } else if(str.charAt(i) == '+') {
                tokens.add(new Pair<>("+", PLUS)); i++;
            } else if(str.charAt(i) == '-') {
                tokens.add(new Pair<>("-", MINUS)); i++;
            } else if(str.charAt(i) == '*') {
                tokens.add(new Pair<>("*", MULTIPLY)); i++;
            } else if(str.charAt(i) == '/') {
                tokens.add(new Pair<>("/", DIVIDE)); i++;
            } else if(str.charAt(i) == '^') {
                tokens.add(new Pair<>("^", CARET)); i++;
            } else if(Character.isLetter(str.charAt(i))) {// a variable
                tokens.add(new Pair<>(Character.toString(str.charAt(i)), VARIABLE)); i++;
            } else throw new NumberFormatException("Expression: Illegal character \"" + str.charAt(i) + "\"");
        }


        // Validate tokens
        ArrayList<Pair<String, Integer>> newTokens = new ArrayList<>();
        for(int i = 0, depth = 0; i < tokens.size(); i++) {
            Pair<String, Integer> token = tokens.get(i);
            Pair<String, Integer> prevToken = (newTokens.size() > 0 ? newTokens.get(newTokens.size()-1) : null);
            switch(token.second()) {
                case NUMBER_LITERAL:
                assert(prevToken == null || prevToken.second() != NUMBER_LITERAL);
                if(prevToken != null && (// implied multiplication
                    prevToken.second() == CLOSE_BRACKET ||
                    prevToken.second() == VARIABLE)) newTokens.add(new Pair<>("*", MULTIPLY));
                newTokens.add(token);
                break;

                case OPEN_BRACKET:
                depth++;
                if(prevToken != null && (// implied multiplication
                    prevToken.second() == NUMBER_LITERAL ||
                    prevToken.second() == CLOSE_BRACKET ||
                    prevToken.second() == VARIABLE)) newTokens.add(new Pair<>("*", MULTIPLY));
                newTokens.add(token);
                break;

                case CLOSE_BRACKET:
                depth--;
                if(depth < 0) throw new NumberFormatException("Expression: Mismatched Brackets");
                if(prevToken != null && prevToken.second() == OPEN_BRACKET) throw new NumberFormatException("Expression: Empty Bracket");// empty brackets
                if(prevToken != null && !(prevToken.second() == NUMBER_LITERAL || prevToken.second() == CLOSE_BRACKET || prevToken.second() == VARIABLE)) throw new NumberFormatException();
                newTokens.add(token);
                break;

                case PLUS:
                if(prevToken != null && (// plus signs are sometimes unnecessary
                    prevToken.second() == NUMBER_LITERAL ||
                    prevToken.second() == CLOSE_BRACKET ||
                    prevToken.second() == VARIABLE)) newTokens.add(token);
                break;

                case MINUS:
                if(prevToken != null && prevToken.second() == PLUS) {// +- becomes -
                    newTokens.remove(newTokens.size()-1);
                    newTokens.add(new Pair<>("-", MINUS));
                } else if(prevToken != null && prevToken.second() == MINUS) {// -- becomes +
                    newTokens.remove(newTokens.size()-1);
                    newTokens.add(new Pair<>("+", PLUS));
                } else newTokens.add(token);
                break;

                case MULTIPLY:
                case DIVIDE:
                case CARET:
                if(prevToken == null) throw new NumberFormatException();
                if(!(prevToken.second() == NUMBER_LITERAL || prevToken.second() == CLOSE_BRACKET || prevToken.second() == VARIABLE)) throw new NumberFormatException();
                newTokens.add(token);
                break;

                case VARIABLE:
                if(prevToken != null && (// implied multiplication
                    prevToken.second() == NUMBER_LITERAL ||
                    prevToken.second() == CLOSE_BRACKET ||
                    prevToken.second() == VARIABLE)) newTokens.add(new Pair<>("*", MULTIPLY));
                newTokens.add(token);
                break;
            }
            if(i == tokens.size()-1) {
                switch(newTokens.get(newTokens.size()-1).second()) {// check validity of last token
                    case NUMBER_LITERAL:
                    case CLOSE_BRACKET:
                    case VARIABLE: break;
                    case OPEN_BRACKET: throw new NumberFormatException("Expression: Mismatched Brackets");
                    default: throw new NumberFormatException();
                }
                for(int j = 0; j < depth; j++) newTokens.add(new Pair<>(")", CLOSE_BRACKET));// add closing brackets if missing
            }
        }

        return newTokens;
    }

    /**
     * Turns a list of tokens into an expression, following the order of operations.
     * @param tokens The tokens to parse.
     * @param beginIndex The index of the first token to parse.
     * @return The parsed Expression.
     */
    private static Expression internalParse(ArrayList<? extends Object> tokens, int beginIndex) {

        // Find the range [beginIndex, endIndex) of the current expression in str
        int endIndex = beginIndex;
        for(int currDepth = 0; endIndex < tokens.size(); endIndex++) {
            if(tokens.get(endIndex) instanceof Pair) {
                Pair<String, Integer> token = (Pair<String, Integer>)tokens.get(endIndex);
                if(token.second() == OPEN_BRACKET) currDepth++;
                else if(token.second() == CLOSE_BRACKET) {
                    currDepth--;
                    if(currDepth < 0) break;
                }
            }
        }
        if(endIndex-beginIndex == 1) return (Expression)tokens.get(beginIndex);

        // Parse brackets
        ArrayList<Object> newTokens = new ArrayList<>();
        for(int i = beginIndex, currDepth = 0; i < endIndex; i++) {
            if(tokens.get(i) instanceof Pair) {
                Pair<String, Integer> token = (Pair<String, Integer>)tokens.get(i);
                if(token.second() == OPEN_BRACKET) {
                    if(currDepth == 0) newTokens.add(internalParse(tokens, i+1));
                    currDepth++;
                }
                if(currDepth == 0) newTokens.add(token);
                if(token.second() == CLOSE_BRACKET) currDepth--;
            } else if(currDepth == 0) newTokens.add(tokens.get(i));
        }
        if(newTokens.size() == 1) return (Expression)newTokens.get(0);

        // Parse exponents right to left (power towers are evaluated top to bottom)
        tokens = newTokens;
        newTokens = new ArrayList<>();
        for(int i = tokens.size()-1; i >= 0; i--) {
            if(tokens.get(i) instanceof Pair) {
                Pair<String, Integer> token = (Pair<String, Integer>)tokens.get(i);
                if(token.second() == CARET) {
                    Expression base = (Expression)tokens.get(i-1);
                    Expression exponent;
                    if(newTokens.get(newTokens.size()-1) instanceof Pair) {// negative sign
                        if(newTokens.get(newTokens.size()-2) instanceof BigRational) {
                            exponent = ((BigRational)newTokens.get(newTokens.size()-2)).negate();
                        } else {
                            ArrayList<Pair<Expression, Integer>> terms = new ArrayList<>();
                            terms.add(new Pair<>((Expression)newTokens.get(newTokens.size()-2), -1));
                            exponent = Sum.parseSum(terms);
                        }
                        newTokens.remove(newTokens.size()-1);
                        newTokens.remove(newTokens.size()-1);
                    } else {
                        exponent = (Expression)newTokens.get(newTokens.size()-1);
                        newTokens.remove(newTokens.size()-1);
                    }
                    newTokens.add(Power.parsePower(base, exponent));
                    i--;// skip the base
                } else newTokens.add(token);
            } else newTokens.add(tokens.get(i));
        }
        if(newTokens.size() == 1) return (Expression)newTokens.get(0);
        Collections.reverse(newTokens);// reverse list since we processed it right to left

        // Parse multiplication and division left to right
        tokens = newTokens;
        newTokens = new ArrayList<>();
        for(int i = 0; i < tokens.size(); i++) {
            if(tokens.get(i) instanceof Pair) {
                Pair<String, Integer> token = (Pair<String, Integer>)tokens.get(i);
                if(token.second() == MULTIPLY || token.second() == DIVIDE) {
                    ArrayList<Expression> factors = new ArrayList<>();
                    ArrayList<Expression> divisors = new ArrayList<>();
                    factors.add((Expression)newTokens.get(newTokens.size()-1));
                    for(; i < tokens.size(); i++) {
                        token = (Pair<String, Integer>)tokens.get(i);
                        if(!(token.second() == MULTIPLY || token.second() == DIVIDE)) {
                            i--; break;
                        }
                        Expression multiplicand;
                        if(tokens.get(i+1) instanceof Pair) {// negative sign
                            if(tokens.get(i+2) instanceof BigRational) {
                                multiplicand = ((BigRational)tokens.get(i+2)).negate();
                            } else {
                                ArrayList<Pair<Expression, Integer>> terms = new ArrayList<>();
                                terms.add(new Pair<>((Expression)tokens.get(i+2), -1));
                                multiplicand = Sum.parseSum(terms);
                            }
                            i++;// skip negative sign
                        } else multiplicand = (Expression)tokens.get(i+1);
                        if(token.second() == MULTIPLY) factors.add(multiplicand);
                        else divisors.add(multiplicand);
                        i++;// skip multiplicand
                    }
                    newTokens.remove(newTokens.size()-1);
                    newTokens.add(Product.parseProduct(factors, divisors));
                } else newTokens.add(tokens.get(i));
            } else newTokens.add(tokens.get(i));
        }
        if(newTokens.size() == 1) return (Expression)newTokens.get(0);

        // Parse addition and subtraction
        tokens = newTokens;
        ArrayList<Pair<Expression, Integer>> terms = new ArrayList<>();
        for(int i = 0; i < tokens.size(); i++) {
            if(tokens.get(i) instanceof Expression) {
                Expression addend = (Expression)tokens.get(i);
                int leadingSign = 1;
                if(i > 0) {
                    Pair<String, Integer> prevToken = (Pair<String, Integer>)tokens.get(i-1);
                    if(prevToken.second() == MINUS) leadingSign = -1;
                }
                terms.add(new Pair<>(addend, leadingSign));
            }
        }
        return Sum.parseSum(terms);
    }

    /**
     * Surrounds the provided {@code String} with a pair of round brackets.
     * @param str The provided {@code String}.
     * @return A {@code String}.
     */
    final protected static String surroundInBrackets(String str) {
        return new StringBuilder("(").append(str).append(')').toString();
    }

    /**
     * Surrounds the provided {@code String} with a pair of curly brackets.
     * @param str The provided {@code String}.
     * @return A {@code String}.
     */
    final protected static String surroundInCurlyBrackets(String str) {
        return new StringBuilder("{").append(str).append('}').toString();
    }

    /**
     * Parses the {@code String} argument into a mathematical expression.
     * @param str The {@code String} representation of the expression.
     * @return An {@code Expression} object.
     */
    public static Expression parse(String str) {
        ArrayList<Pair<String, Integer>> tokens = tokenizeExpression(str);

        // parse literals
        ArrayList<Object> newTokens = new ArrayList<>();
        for(int i = 0; i < tokens.size(); i++) {
            Pair<String, Integer> token = tokens.get(i);
            if(token.second() == NUMBER_LITERAL) {// parse number literals
                newTokens.add(BigRational.parseNumber(token.first()));
            } else if(token.second() == VARIABLE) {// parse variables
                newTokens.add(new Variable(token.first()));
            } else newTokens.add(token);
        }

        return internalParse(newTokens, 0);
    }

// <-------------------- Methods Overriden from java.lang.Object -------------------->

    /**
     * Compares this Expression with the specified object for equality.
     * @param o The object to which this Expression is to be compared.
     * @return True if the object is a Expression and whose value is numerically
     * equal to this Expression.
     */
    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    /**
     * Returns a {@code String} object representing the value of this Expression.
     */
    @Override
    public abstract String toString();

// <---------------------------------- Own Methods ---------------------------------->

    /**
     * Returns an Expression whose value is {@code (this + expression)}.
     *
     * @param  expression The value to be added to this Expression.
     * @return {@code this + expression}
     */
    public Expression add(Expression expression) {
        ArrayList<Pair<Expression, Integer>> terms = new ArrayList<>();
        terms.add(new Pair<>(this, 1));
        terms.add(new Pair<>(expression, 1));
        return Sum.parseSum(terms);
    }

    /**
     * Returns an Expression whose value is {@code (this / expression)}.
     *
     * @param  expression The value by which this Expression is to be divided.
     * @return {@code this / expression}
     * @throws ArithmeticException if {@code expression} simplifies to zero.
     */
    public Expression divide(Expression expression) {
        ArrayList<Expression> factors = new ArrayList<>();
        ArrayList<Expression> divisors = new ArrayList<>();
        factors.add(this);
        divisors.add(expression);
        return Product.parseProduct(factors, divisors);
    }

    /**
     * Returns an Expression whose value is {@code (this * expression)}.
     *
     * @param  expression The value to be multiplied by this Expression.
     * @return {@code this * expression}
     */
    public Expression multiply(Expression expression) {
        ArrayList<Expression> factors = new ArrayList<>();
        factors.add(this);
        factors.add(expression);
        return Product.parseProduct(factors, new ArrayList<>());
    }

    /**
     * Returns a BigRational whose value is {@code (-this)}.
     *
     * @return {@code -this}
     */
    public Expression negate() {
        return multiply(BigRational.NEGATIVE_ONE);
    }

    /**
     * Returns an Expression whose value is {@code this ^ expression}.
     *
     * @param expression The exponent to which this Expression is to be raised.
     * @return {@code this ^ expression}
     */
    public Expression pow(Expression expression) {
        return Power.parsePower(this, expression);
    }

    /**
     * Returns an Expression whose value is {@code (1 / this)}.
     * @return {@code 1 / this}.
     */
    public Expression reciprocal() {
        ArrayList<Expression> terms = new ArrayList<>();
        terms.add(this);
        return Product.parseProduct(new ArrayList<>(), terms);
    }

    /**
     * Returns an Expression whose value is {@code (this - expression)}.
     *
     * @param expression The value to be subtracted from this Expression.
     * @return {@code this - expression}
     */
    public Expression subtract(Expression expression) {
        ArrayList<Pair<Expression, Integer>> terms = new ArrayList<>();
        terms.add(new Pair<>(this, 1));
        terms.add(new Pair<>(expression, -1));
        return Sum.parseSum(terms);
    }


    /**
     * Returns the LaTeX String representation of this Expression.
     * @return A string.
     */
    public abstract String toLatexString();

    /**
     * Returns the String representation of this Expression in function form.
     * @return A string.
     */
    public abstract String toFunctionString();

    /**
     * Attempts to compute a numerical exact value for the current expression.
     * @return The result of evaluating the expression.
     * @throws ArithmeticException If variables are present in the expression.
     */
    public Expression evaluate() {
        return this.evaluate(new ArrayList<>());
    }

    /**
     * Attempts to compute a numerical exact value for this Expression, given values of the variables.
     * @param variableValues The values to substitute into the variables.
     * @return The result of evaluating the expression.
     * @throws ArithmeticException If the value of a variable in the expression is not provided.
     */
    public Expression evaluate(ArrayList<Pair<String, Expression>> variableValues) {
        HashMap<String, Expression> variables = new HashMap<>();
        for(Pair<String, Expression> variable: variableValues) {
            variables.putIfAbsent(variable.first(), variable.second());
        }
        return internalEvaluate(variables);
    }

    /**
     * Attempts to compute a numerical exact value for the current expression, given values of the variables.
     * @param variableValues The values to substitute into the variables.
     * @return The result of evaluating the expression.
     * @throws ArithmeticException If the value of a variable in the expression is not provided.
     */
    protected abstract Expression internalEvaluate(HashMap<String, Expression> variableValues);

    /**
     * Attempts to reduce the complexity of this Expression by manipulating it algebraically.
     * @return A simplified expression that is equivalent to this Expression.
     */
    public abstract Expression simplify();
}
