# TestNG plugin

This plugin is a TestNG runner for [Play Framework 2.5](http://www.playframework.org/).
It using the [TestNG sbt interface by jmhofer](https://bitbucket.org/jmhofer/sbt-testng-interface) to run TestNG Test suites, and adds Helpers.
Example can be found in the `sample` folder.

## Usage

### Installation

#### Plugin

Add the plugin in your `project/plugins.sbt` file.

```scala
addSbtPlugin("com.linkedin.play-testng-plugin" % "play-plugins-testng" % "2.5.0")
```

Add the following to your project's `build.sbt` file:

```scala
import com.linkedin.plugin.NGPlugin
lazy val {{project name}} = (project in file ("."))
  .settings(
    {{your settings}}
  )
  {{{enable plugins, set dependencies, etc.}}}
  .enablePlugins(NGPlugin)
```

#### Helpers

Add the dependency in your `build.sbt` file.

```scala
val appDependencies = Seq(
 "com.linkedin.play-testng-plugin" %% "play-testng-helpers" % "2.5.0"
)
```

***Test classes must extend NGTests to use the helpers.***
***Use `com.linkedin.plugin.j.*` in Java code and `com.linkedin.plugin.s.*` from Scala code.***

### Annotations

#### @WithFakeApplication

`@WithFakeApplication` runs the test in the context of a Play application.

It's equivalent to:

```java
running(fakeApplication(), new Runnable() {
  public void run() {
    // Your
  }
});
```

This Annotation can be used on Test methods and test classes.
When it's used on a class, a fake application created via a default `GuiceApplicationBuilder` will be started at the beginning of each test, and stop when the test finishes.

```java
@WithFakeApplication
public class AllWithFakeApp extends NGTests {
 @Test
 public void aFastTest() {
    String f = play.Play.application().configuration().getString("test.loutre");
    if(!f.equals("oink"))
       throw new RuntimeException("Assertion failed");
 }
}
```

Or

```java
public class SimpleTest extends NGTests {
 @Test
 @WithFakeApplication
 public void aFailingTest() {
   String f = play.Play.application().configuration().getString("test.fakeconf");
   if(!f.equals("fake"))
     throw new RuntimeException("Assertion failed");
 }
}
```

#### @Conf

You can change the application configuration for testing purpose using the `@Conf` annotation:

```java
public class SimpleTest extends NGTests {
 
 @Test
 public void aFastTest() {
    System.out.println("Fast test");
 }
 
 @Test
 @WithFakeApplication
 @Conf(key="test.fakeconf", value="fake") // wil override the application.conf value for "test.fakeconf"
 public void aFailingTest() {
   String f = play.Play.application().configuration().getString("test.fakeconf");
   if(!f.equals("fake"))
     throw new RuntimeException("Assertion failed");
 }
}
```
Alternatively, if you need to change multiple configuration entries, you can wrap those `@Conf` into a `@Confs`:

```java
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
 public void aFailingTest() {
   String f = play.Play.application().configuration().getString("test.fakeconf");
   if(!f.equals("fake"))
     throw new RuntimeException("Assertion failed");
 }
}
```

The `@Conf` and `@Confs` annotation also work on test Classes. When used on classes, the new configuration will be used for **every** test in this class:

```java
@WithFakeApplication
@Conf(key="test.fakeconf", value="fake")
public class AllWithFakeApp extends NGTests {
 @Test
 public void anotherFastTest() {
   String f = play.Play.application().configuration().getString("test.fakeconf");
   if(!f.equals("fake"))
     throw new RuntimeException("Assertion failed");
 }
}
```

Declaration on classes and methods can be mixed:

```java
@WithFakeApplication
@Confs({
  @Conf(key="test.fakeconf", value="fake"),
  @Conf(key="test.anotherConf", value="fake")
})
public class AllWithFakeApp extends NGTests {
 
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
```

**Note:** `@Confs` and `@Conf` will be ignored if tests (class or method) are not annotated with `@WithFakeApplication`.

## License

Copyright 2012–2017 LinkedIn

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
