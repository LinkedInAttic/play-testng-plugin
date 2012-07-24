import org.testng.annotations.*;

import play.mvc.*;
import play.test.*;
import play.libs.F.*;

import static play.test.Helpers.*;

import com.linkedin.plugin.*;

@WithFakeApplication
@Confs({
  @Conf(key="test.fakeconf", value="fake")
})
@Conf(key="test.anotherConf", value="fake")
@WithPlugins({"plugins.DummyPlugin"})
public class AllWithFakeApp extends NGTests {

 @Test
 @Confs({
   @Conf(key="test.loutre", value="oink")
 })
 public void aFastTest() {
    String f = play.Play.application().configuration().getString("test.loutre");
    if(!f.equals("oink"))
       throw new RuntimeException("Assertion failed");
 }
 
 @Test
 public void anotherFastTest() {
   String f = play.Play.application().configuration().getString("test.fakeconf");
   if(!f.equals("fake"))
     throw new RuntimeException("Assertion failed");
 }
 
 @Test
 public void testAnotherConf() {
   String f = play.Play.application().configuration().getString("test.anotherConf");
   if(!f.equals("fake"))
     throw new RuntimeException("Assertion failed");
 }
 
 @Test
 @Conf(key="test.singleConf", value="fake")
 public void testASingleConf() {
   String f = play.Play.application().configuration().getString("test.singleConf");
   if(!f.equals("fake"))
     throw new RuntimeException("Assertion failed");
 }

}
