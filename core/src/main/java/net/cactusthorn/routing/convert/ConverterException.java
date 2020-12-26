package net.cactusthorn.routing.convert;

public class ConverterException extends Exception {

    private static final long serialVersionUID = 0L;

    public ConverterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConverterException(String message, Throwable cause, Object param) {
        super(String.format(message, param), cause);
    }

    public ConverterException(Throwable cause) {
        super(cause);
    }
}
