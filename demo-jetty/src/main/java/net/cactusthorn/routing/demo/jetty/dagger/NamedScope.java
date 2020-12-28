package net.cactusthorn.routing.demo.jetty.dagger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

@Qualifier @Retention(RetentionPolicy.RUNTIME) //
public @interface NamedScope {
    Class<?> value();
}