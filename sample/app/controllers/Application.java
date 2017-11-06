package controllers;

import java.util.*;
import play.*;
import play.mvc.*;
import views.html.*;

public class Application extends Controller {

    /**
     * Home page
     */
    public Result index() {
        return ok("Helloworld");
    }
}