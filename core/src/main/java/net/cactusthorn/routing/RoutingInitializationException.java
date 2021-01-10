package net.cactusthorn.routing;

public class RoutingInitializationException extends RuntimeException {

    private static final long serialVersionUID = 0L;

    public RoutingInitializationException(String message, Object param) {
        super(String.format(message, param));
    }

    public RoutingInitializationException(String message, Throwable cause, Object param) {
        super(String.format(message, param), cause);
    }

    public RoutingInitializationException(String message, Object param1, Object param2) {
        super(String.format(message, param1, param2));
    }

    public RoutingInitializationException(String message, Object param1, Object param2, Object param3) {
        super(String.format(message, param1, param2, param3));
    }
}
