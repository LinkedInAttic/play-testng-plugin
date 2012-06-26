import org.testng.annotations.*;

import play.mvc.*;
import play.test.*;
import play.libs.F.*;

import static play.test.Helpers.*;

import com.linkedin.plugin.*;

public class SimpleTest extends LITests {
    
 @Test
 public void aFastTest() {
    System.out.println("Fast test");
 }
 
 @Test
 @WithFakeApplication
 @Confs({
    @Conf(key="test.fakeconf", value="fake"),
    @Conf(key="test.loutre", value="oink")
 })
 public void aFailingTest() {
   String f = play.Play.application().configuration().getString("test.fakeconf");
   if(!f.equals("fake"))
     throw new RuntimeException("Assertion failed");
 }
}
