package net.cactusthorn.routing.convert;

public class ConverterException extends Exception {

    private static final long serialVersionUID = 0L;

    private static final String MESSAGE = "Parameter position: %s; Parameter type: %s; %s";

    public ConverterException(Throwable cause, int position, String type) {
        super(String.format(MESSAGE, position, type, cause), cause);
    }
}
