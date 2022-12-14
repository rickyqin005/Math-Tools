import java.util.Scanner;

import arithmetic.Expression;

public class testing {
    public static Scanner in = new Scanner(System.in);
    public static void main(String[] args) {
        int N = in.nextInt(); in.nextLine();
        for(int i = 0; i < N; i++) {
            try {
                String str = in.nextLine();
                Expression expression = Expression.parse(str);
                System.out.println("= " + expression);
                System.out.println("= " + expression.toLatexString());
                System.out.println("= " + expression.toFunctionString());
            } catch (Exception e) {
                System.out.println(e);
            }
            System.out.println();
        }
    }
}
