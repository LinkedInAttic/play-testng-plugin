package com.linkedin.plugin;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Target(ElementType.METHOD)
public @interface FakeApplication {
  String conf() default "";
}