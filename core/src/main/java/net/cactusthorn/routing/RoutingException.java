package net.cactusthorn.routing;

public class RoutingException extends RuntimeException {

    private static final long serialVersionUID = 0L;

    public RoutingException(String message, Throwable cause) {
        super(message, cause);
    }
}
