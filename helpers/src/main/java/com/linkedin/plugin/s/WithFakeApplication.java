// Copyright 2012 LinkedIn
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.linkedin.plugin.s;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface WithFakeApplication {
  String path() default ".";
  @Deprecated Class<? extends play.api.GlobalSettings> withGlobal() default play.api.GlobalSettings.class;
  Class<? extends play.api.inject.guice.GuiceBuilder> guiceBuilder() default play.api.inject.guice.GuiceApplicationBuilder.class;
  Class<? extends FakeApplicationFactory> appFactory() default FakeApplicationFactoryImpl.class;
}