package net.cactusthorn.routing;

public class RoutingInitializationException extends RuntimeException {

    private static final long serialVersionUID = 0L;

    public RoutingInitializationException(String message) {
        super(message);
    }

    public RoutingInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
