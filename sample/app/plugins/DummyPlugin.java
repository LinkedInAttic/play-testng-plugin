package plugins;

import play.*;
import play.db.*;

public class DummyPlugin extends Plugin {
    
    private final Application application;
    
    public DummyPlugin(Application application) {
        this.application = application;
    }

    public void onStart() {
      System.out.println("Starting plugin " + this.getClass() + " in application " + application);
    }
    
    public void onStop() {
      System.out.println("Stoping plugin " + this.getClass() + " in application " + application);
    }

}