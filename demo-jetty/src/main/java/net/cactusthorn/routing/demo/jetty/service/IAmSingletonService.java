package net.cactusthorn.routing.demo.jetty.service;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton //
public class IAmSingletonService {

    @Inject //
    public IAmSingletonService() {
    }

    public String iam() {
        return this.getClass().getSimpleName() + "@" + this.hashCode();
    }
}
