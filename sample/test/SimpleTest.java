import com.linkedin.plugin.j.WithFakeApplication;
import org.testng.annotations.*;

import play.mvc.*;
import play.test.*;
import play.libs.F.*;

import static play.test.Helpers.*;

import com.linkedin.plugin.*;
import com.linkedin.plugin.j.*;

public class SimpleTest extends NGTests {
    
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
 @WithPlugins({"plugins.DummyPlugin"})
 public void aFailingTest() {
   String f = play.Play.application().configuration().getString("test.fakeconf");
   if(!f.equals("fake"))
     throw new RuntimeException("Assertion failed");
 }
}
