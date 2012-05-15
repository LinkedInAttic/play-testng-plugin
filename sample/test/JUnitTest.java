import org.junit.Test;
import static org.junit.Assert.*;

public class JUnitTest {
 
 @Test
 public void aFastTest() {
   System.out.println("Fast test");
 }
 
 @Test
 public void aFailingTest() {
    assertTrue(false);
    System.out.println("failing test");
 }
 
}
