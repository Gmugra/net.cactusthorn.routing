package net.cactusthorn.routing.convert;

public class ConverterException extends Exception {

    private static final long serialVersionUID = 0L;

    public ConverterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConverterException(Throwable cause) {
        super(cause);
    }
}
