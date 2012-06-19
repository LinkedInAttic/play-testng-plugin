import org.testng.annotations.*;

import play.mvc.*;
import play.test.*;
import play.libs.F.*;

import static play.test.Helpers.*;
 
public class SimpleTest {
 
 @Test
 public void aFastTest() {
   running(fakeApplication(inMemoryDatabase()), new Runnable() {
      public void run() {
          System.out.println("Fast test");
      }
   });
 }
 
 @Test
 @com.linkedin.plugin.FakeApplication
 public void aFailingTest() {
    if(true) throw new RuntimeException("FAIL");
    System.out.println("failing test");
 }
 
}
