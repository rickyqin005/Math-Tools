import java.util.Scanner;

import algebra.BigRational;
import algebra.Expression;

public class testing {
    public static Scanner in = new Scanner(System.in);
    public static BigRational readBigRational(String name) {
        System.out.print("Enter a rational number " + name + " = A/B in the format \"A B\":");
        return new BigRational(in.next(), in.next());
    }
    public static void main(String[] args) {
        while(true) {
            try {
                String expression = in.nextLine();
                System.out.println(BigRational.parseNumber(expression));
                // Expression.parse(expression);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}

// "   5*(22 /4 + 5*(6/5 - 3/2))^(-5) - 2*(5/9) "
