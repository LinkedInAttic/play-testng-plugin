package controllers;

import play.*;
import play.mvc.*;
import play.data.*;
import play.data.validation.Constraints.*;

import java.util.*;

import views.html.*;

public class Application extends Controller {
    
   
  
    /**
     * Home page
     */
    public static Result index() {
        return ok("Helloworld");
    }
  
  
  
}