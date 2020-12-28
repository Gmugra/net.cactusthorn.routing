package net.cactusthorn.routing.demo.jetty.dagger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Scope;

@Scope @Retention(RetentionPolicy.RUNTIME) //
public @interface SessionScope {
}
