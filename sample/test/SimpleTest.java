import org.testng.annotations.*;
 
public class SimpleTest {
 
 @Test
 public void aFastTest() {
   System.out.println("Fast test");
 }
 
 @Test
 public void aSlowTest() {
    System.out.println("Slow test");
 }
 
}
