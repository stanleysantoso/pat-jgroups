import org.junit.Test;

import java.util.Stack;

import static org.junit.Assert.*;

/**
 * Created by Stanley on 10/26/2015.
 */
public class ReplStackTest {
    ReplStack test = new ReplStack();
    Stack<Object> stack = new Stack<Object>();
    @Test
    public void testMain() throws Exception {
        test.push("Foo");
        stack.push("Foo");
        assertEquals("Add", stack, test.getStack());
        assertEquals("Contains",stack.peek(),test.top());
        assertEquals("Remove",stack.pop(),test.pop());
    }
}