import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * Created by Stanley on 10/26/2015.
 */
public class ReplSetTest {
    ReplSet test = new ReplSet();
    HashSet<Object> set = new HashSet<Object>();
    @Test
    public void testMain() throws Exception {
        test.add("Foo");
        set.add("Foo");
        assertEquals("Add", set, test.getSet());
        assertEquals("Contains",set.contains("Foo"),test.contains("Foo"));
        test.remove("Foo");
        set.remove("Foo");
        assertEquals("Remove",set,test.getSet());
    }
}