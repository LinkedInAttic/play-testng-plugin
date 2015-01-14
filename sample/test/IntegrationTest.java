import org.testng.annotations.*;

import play.mvc.*;
import play.test.*;
import play.libs.F.*;

import static play.test.Helpers.*;
import static org.testng.AssertJUnit.*;

import static org.fluentlenium.core.filter.FilterConstructor.*;

import com.linkedin.plugin.*;
import com.linkedin.plugin.j.*;

@WithTestServer
public class IntegrationTest extends NGTests { 
    @Test
    @WithTestServer
    public void test() {
      browser().goTo("http://localhost:3333");
      assertTrue(browser().pageSource().contains("Helloworld"));
    }
}
