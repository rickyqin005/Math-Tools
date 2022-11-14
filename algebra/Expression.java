package algebra;

import java.util.ArrayList;
import java.util.Collections;

import utility.Pair;

public abstract class Expression {
    final public static char LEFT_ROUND_BRACKET = '(';
    final public static char RIGHT_ROUND_BRACKET = ')';
    final private static int NUMBER_LITERAL = 1;
    final private static int OPEN_BRACKET = 2;
    final private static int CLOSE_BRACKET = 3;
    final private static int PLUS = 4;
    final private static int MINUS = 5;
    final private static int MULTIPLY = 6;
    final private static int DIVIDE = 7;
    final private static int CARET = 8;

    private static ArrayList<Pair<String, Integer>> tokenizeExpression(String str) {
        // Remove whitespace characters
        str = str.replaceAll("\\s", "");


        // Split the string into tokens
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
            } else throw new RuntimeException("Illegal character\"" + str.charAt(i) + "\"");
        }
        // System.out.println("First round of token parsing:");
        // for(Pair<String, Integer> token: tokens) System.out.println(token.first + "    " + token.second);
        // System.out.println();


        // Validate tokens
        ArrayList<Pair<String, Integer>> newTokens = new ArrayList<>();
        for(int i = 0, depth = 0; i < tokens.size(); i++) {
            Pair<String, Integer> token = tokens.get(i);
            Pair<String, Integer> prevToken = (newTokens.size() > 0 ? newTokens.get(newTokens.size()-1) : null);
            switch(token.second) {
                case NUMBER_LITERAL:
                assert(prevToken == null || prevToken.second != NUMBER_LITERAL);
                if(prevToken != null && prevToken.second == CLOSE_BRACKET) newTokens.add(new Pair<>("*", MULTIPLY));
                newTokens.add(token);
                break;

                case OPEN_BRACKET:
                depth++;
                if(prevToken != null && (prevToken.second == NUMBER_LITERAL || prevToken.second == CLOSE_BRACKET)) {
                    newTokens.add(new Pair<>("*", MULTIPLY));
                }
                newTokens.add(token);
                break;

                case CLOSE_BRACKET:
                depth--;
                if(depth < 0) throw new RuntimeException("Mismatched Brackets");
                if(prevToken != null && prevToken.second == OPEN_BRACKET) throw new RuntimeException("Empty Bracket");// empty brackets
                if(prevToken != null && !(prevToken.second == NUMBER_LITERAL || prevToken.second == CLOSE_BRACKET)) throw new RuntimeException();
                newTokens.add(token);
                break;

                case PLUS:
                if(prevToken != null && (prevToken.second == NUMBER_LITERAL || prevToken.second == CLOSE_BRACKET)) newTokens.add(token);
                break;

                case MINUS:
                if(prevToken != null && prevToken.second == PLUS) {
                    newTokens.remove(newTokens.size()-1);
                    newTokens.add(token);
                } else if(prevToken != null && prevToken.second == MINUS) {
                    newTokens.remove(newTokens.size()-1);
                    newTokens.add(new Pair<>("+", PLUS));
                } else newTokens.add(token);
                break;

                case MULTIPLY:
                if(prevToken == null) throw new RuntimeException();
                if(!(prevToken.second == NUMBER_LITERAL || prevToken.second == CLOSE_BRACKET)) throw new RuntimeException();
                newTokens.add(token);
                break;

                case DIVIDE:
                if(prevToken == null) throw new RuntimeException();
                if(!(prevToken.second == NUMBER_LITERAL || prevToken.second == CLOSE_BRACKET)) throw new RuntimeException();
                newTokens.add(token);
                break;

                case CARET:
                if(prevToken == null) throw new RuntimeException();
                if(!(prevToken.second == NUMBER_LITERAL || prevToken.second == CLOSE_BRACKET)) throw new RuntimeException();
                newTokens.add(token);
                break;
            }
            if(i == tokens.size()-1) {
                switch(newTokens.get(newTokens.size()-1).second) {// check validity of last token
                    case NUMBER_LITERAL:
                    case CLOSE_BRACKET: break;
                    case OPEN_BRACKET: throw new RuntimeException("Mismatched Brackets");
                    default: throw new RuntimeException();
                }
                for(int j = 0; j < depth; j++) newTokens.add(new Pair<>(")", CLOSE_BRACKET));// add closing brackets if missing
            }
        }
        // System.out.println("Second round of token parsing:");
        // for(Pair<String, Integer> token: newTokens) System.out.println(token.first + "    " + token.second);
        // System.out.println();

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
                        } else exponent = new Sum().add((Expression)newTokens.get(newTokens.size()-2), -1);
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
                    Expression multiplier = (Expression)newTokens.get(newTokens.size()-1);
                    Expression multiplicand;
                    if(tokens.get(i+1) instanceof Pair) {// negative sign
                        if(tokens.get(i+2) instanceof BigRational) {
                            multiplicand = ((BigRational)tokens.get(i+2)).negate();
                        } else multiplicand = new Sum().add((Expression)tokens.get(i+2), -1);
                        i++;// skip negative sign
                    } else multiplicand = (Expression)tokens.get(i+1);
                    Product product;
                    if(multiplier instanceof Product) product = (Product)multiplier;
                    else product = new Product(multiplier, 1);
                    product.add(multiplicand, (token.second == MULTIPLY ? 1 : -1));
                    newTokens.remove(newTokens.size()-1);
                    newTokens.add(product);
                    i++;// skip multiplicand
                } else newTokens.add(tokens.get(i));
            } else newTokens.add(tokens.get(i));
        }
        if(newTokens.size() == 1) return (Expression)newTokens.get(0);
        ((5)((((22)/((4)))+((5)(((6)/((5)))-((3)/((2))))))^(-(5))))-((2)((5)/((9))))
        // Parse addition and subtraction
        tokens = newTokens;
        Sum finalExpression = new Sum();
        for(int i = 0; i < tokens.size(); i++) {
            if(tokens.get(i) instanceof Expression) {
                Expression addend = (Expression)tokens.get(i);
                int leadingSign = 1;
                if(i > 0) {
                    Pair<String, Integer> token = (Pair<String, Integer>)tokens.get(i-1);
                    if(token.second == MINUS) leadingSign = -1;
                }
                finalExpression.add(addend, leadingSign);
            }
        }
        return finalExpression;
    }

    protected static String surroundInBrackets(String str) {
        return new StringBuilder("(").append(str).append(")").toString();
    }

    public static Expression parse(String str) {
        ArrayList<Pair<String, Integer>> tokens = tokenizeExpression(str);

        // parse number literals
        ArrayList<Object> newTokens = new ArrayList<>();
        for(int i = 0; i < tokens.size(); i++) {
            Pair<String, Integer> token = tokens.get(i);
            if(token.second == NUMBER_LITERAL) newTokens.add(BigRational.parseNumber(token.first));
            else newTokens.add(token);
        }

        return internalParse(newTokens, 0);
    }
}
