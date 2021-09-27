import java.util.Collections;
import java.util.Stack;

public class Test {
    public static void main(String[] args) {
        Stack<String> stack = new Stack<>();
        stack.add("Dimas");
        stack.add("St");
        stack.add("23");
        Collections.reverse(stack);
        while(!stack.empty()){
            System.out.println(stack.pop());
        }
    }
}
