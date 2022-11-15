import java.util.Scanner;

import algebra.Expression;

public class testing {
    public static Scanner in = new Scanner(System.in);
    public static void main(String[] args) {
        int N = in.nextInt(); in.nextLine();
        for(int i = 0; i < N; i++) {
            try {
                String str = in.nextLine();
                // long startTime = System.nanoTime();
                Expression expression = Expression.parse(str);
                System.out.println("= " + expression);
                // System.out.println("= " + expression.evaluate());
                System.out.println();
                // System.out.printf("(Took %.03f ms)\n", ((double)(System.nanoTime()-startTime))/1000000);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
