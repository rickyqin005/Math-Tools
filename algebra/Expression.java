package algebra;

import java.util.ArrayList;
import java.util.Collections;

import utility.Pair;

public abstract class Expression {
    final private static int NUMBER_LITERAL = 1;
    final private static int OPEN_BRACKET = 2;
    final private static int CLOSE_BRACKET = 3;
    final private static int PLUS = 4;
    final private static int MINUS = 5;
    final private static int MULTIPLY = 6;
    final private static int DIVIDE = 7;
    final private static int CARET = 8;
    final private static int VARIABLE = 9;

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
                if(integerPartLen == 0 && decimalPartLen == 0) throw new RuntimeException("Illegal character \".\"");

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
            } else throw new RuntimeException("Illegal character \"" + str.charAt(i) + "\"");
        }


        // Validate tokens
        ArrayList<Pair<String, Integer>> newTokens = new ArrayList<>();
        for(int i = 0, depth = 0; i < tokens.size(); i++) {
            Pair<String, Integer> token = tokens.get(i);
            Pair<String, Integer> prevToken = (newTokens.size() > 0 ? newTokens.get(newTokens.size()-1) : null);
            switch(token.second) {
                case NUMBER_LITERAL:
                assert(prevToken == null || prevToken.second != NUMBER_LITERAL);
                if(prevToken != null && (// implied multiplication
                    prevToken.second == CLOSE_BRACKET ||
                    prevToken.second == VARIABLE)) newTokens.add(new Pair<>("*", MULTIPLY));
                newTokens.add(token);
                break;

                case OPEN_BRACKET:
                depth++;
                if(prevToken != null && (// implied multiplication
                    prevToken.second == NUMBER_LITERAL ||
                    prevToken.second == CLOSE_BRACKET ||
                    prevToken.second == VARIABLE)) newTokens.add(new Pair<>("*", MULTIPLY));
                newTokens.add(token);
                break;

                case CLOSE_BRACKET:
                depth--;
                if(depth < 0) throw new RuntimeException("Mismatched Brackets");
                if(prevToken != null && prevToken.second == OPEN_BRACKET) throw new RuntimeException("Empty Bracket");// empty brackets
                if(prevToken != null && !(prevToken.second == NUMBER_LITERAL || prevToken.second == CLOSE_BRACKET || prevToken.second == VARIABLE)) throw new RuntimeException();
                newTokens.add(token);
                break;

                case PLUS:
                if(prevToken != null && (// plus signs are sometimes unnecessary
                    prevToken.second == NUMBER_LITERAL ||
                    prevToken.second == CLOSE_BRACKET ||
                    prevToken.second == VARIABLE)) newTokens.add(token);
                break;

                case MINUS:
                if(prevToken != null && prevToken.second == PLUS) {// +- becomes -
                    newTokens.remove(newTokens.size()-1);
                    newTokens.add(new Pair<>("-", MINUS));
                } else if(prevToken != null && prevToken.second == MINUS) {// -- becomes +
                    newTokens.remove(newTokens.size()-1);
                    newTokens.add(new Pair<>("+", PLUS));
                } else newTokens.add(token);
                break;

                case MULTIPLY:
                case DIVIDE:
                case CARET:
                if(prevToken == null) throw new RuntimeException();
                if(!(prevToken.second == NUMBER_LITERAL || prevToken.second == CLOSE_BRACKET || prevToken.second == VARIABLE)) throw new RuntimeException();
                newTokens.add(token);
                break;

                case VARIABLE:
                if(prevToken != null && (// implied multiplication
                    prevToken.second == NUMBER_LITERAL ||
                    prevToken.second == CLOSE_BRACKET ||
                    prevToken.second == VARIABLE)) newTokens.add(new Pair<>("*", MULTIPLY));
                newTokens.add(token);
                break;
            }
            if(i == tokens.size()-1) {
                switch(newTokens.get(newTokens.size()-1).second) {// check validity of last token
                    case NUMBER_LITERAL:
                    case CLOSE_BRACKET:
                    case VARIABLE: break;
                    case OPEN_BRACKET: throw new RuntimeException("Mismatched Brackets");
                    default: throw new RuntimeException();
                }
                for(int j = 0; j < depth; j++) newTokens.add(new Pair<>(")", CLOSE_BRACKET));// add closing brackets if missing
            }
        }

        return newTokens;
    }

    private static Expression internalParse(ArrayList<? extends Object> tokens, int beginIndex) {

        // Find the range [beginIndex, endIndex) of the current expression in str
        int endIndex = beginIndex;
        for(int currDepth = 0; endIndex < tokens.size(); endIndex++) {
            if(tokens.get(endIndex) instanceof Pair) {
                Pair<String, Integer> token = (Pair<String, Integer>)tokens.get(endIndex);
                if(token.second == OPEN_BRACKET) currDepth++;
                else if(token.second == CLOSE_BRACKET) {
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
                if(token.second == OPEN_BRACKET) {
                    if(currDepth == 0) newTokens.add(internalParse(tokens, i+1));
                    currDepth++;
                }
                if(currDepth == 0) newTokens.add(token);
                if(token.second == CLOSE_BRACKET) currDepth--;
            } else if(currDepth == 0) newTokens.add(tokens.get(i));
        }
        if(newTokens.size() == 1) return (Expression)newTokens.get(0);

        // Parse exponents right to left (power towers are evaluated top to bottom)
        tokens = newTokens;
        newTokens = new ArrayList<>();
        for(int i = tokens.size()-1; i >= 0; i--) {
            if(tokens.get(i) instanceof Pair) {
                Pair<String, Integer> token = (Pair<String, Integer>)tokens.get(i);
                if(token.second == CARET) {
                    Expression base = (Expression)tokens.get(i-1);
                    Expression exponent;
                    if(newTokens.get(newTokens.size()-1) instanceof Pair) {// negative sign
                        if(newTokens.get(newTokens.size()-2) instanceof BigRational) {
                            exponent = ((BigRational)newTokens.get(newTokens.size()-2)).negate();
                        } else {
                            ArrayList<Pair<Expression, Integer>> terms = new ArrayList<>();
                            terms.add(new Pair<>((Expression)newTokens.get(newTokens.size()-2), -1));
                            exponent = new Sum(terms);
                        }
                        newTokens.remove(newTokens.size()-1);
                        newTokens.remove(newTokens.size()-1);
                    } else {
                        exponent = (Expression)newTokens.get(newTokens.size()-1);
                        newTokens.remove(newTokens.size()-1);
                    }
                    newTokens.add(new Power(base, exponent));
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
                if(token.second == MULTIPLY || token.second == DIVIDE) {
                    ArrayList<Expression> factors = new ArrayList<>();
                    ArrayList<Expression> divisors = new ArrayList<>();
                    factors.add((Expression)newTokens.get(newTokens.size()-1));
                    for(; i < tokens.size(); i++) {
                        token = (Pair<String, Integer>)tokens.get(i);
                        if(!(token.second == MULTIPLY || token.second == DIVIDE)) {
                            i--; break;
                        }
                        Expression multiplicand;
                        if(tokens.get(i+1) instanceof Pair) {// negative sign
                            if(tokens.get(i+2) instanceof BigRational) {
                                multiplicand = ((BigRational)tokens.get(i+2)).negate();
                            } else {
                                ArrayList<Pair<Expression, Integer>> terms = new ArrayList<>();
                                terms.add(new Pair<>((Expression)tokens.get(i+2), -1));
                                multiplicand = new Sum(terms);
                            }
                            i++;// skip negative sign
                        } else multiplicand = (Expression)tokens.get(i+1);
                        if(token.second == MULTIPLY) factors.add(multiplicand);
                        else divisors.add(multiplicand);
                        i++;// skip multiplicand
                    }
                    newTokens.remove(newTokens.size()-1);
                    newTokens.add(new Product(factors, divisors));
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
                    if(prevToken.second == MINUS) leadingSign = -1;
                }
                terms.add(new Pair<>(addend, leadingSign));
            }
        }
        return new Sum(terms);
    }

    protected static String surroundInBrackets(String str) {
        return new StringBuilder("(").append(str).append(")").toString();
    }

    public static Expression parse(String str) {
        ArrayList<Pair<String, Integer>> tokens = tokenizeExpression(str);

        // parse literals
        ArrayList<Object> newTokens = new ArrayList<>();
        for(int i = 0; i < tokens.size(); i++) {
            Pair<String, Integer> token = tokens.get(i);
            if(token.second == NUMBER_LITERAL) {// parse number literals
                newTokens.add(BigRational.parseNumber(token.first));
            } else if(token.second == VARIABLE) {// parse variables
                newTokens.add(new Variable(token.first));
            } else newTokens.add(token);
        }

        return internalParse(newTokens, 0);
    }

    public abstract Expression evaluate();

    // public abstract Expression evaluate(ArrayList<Pair<String, Expression>> variableValues);
}
