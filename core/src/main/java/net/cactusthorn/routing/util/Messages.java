package net.cactusthorn.routing.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public final class Messages {

    private static final String BANDLE = Messages.class.getName();
    private static final ResourceBundle MESSAGES = ResourceBundle.getBundle(BANDLE, Locale.getDefault());

    public enum Key {
        APPEND_OPAQUE_URI,
        BODY_READER_NOT_FOUND,
        CANT_BE_COLLECTION,
        CONTEXT_NOT_SUPPORTED,
        ERROR_AT_PARAMETER_POSITION,
        ERROR_METHOD_INVOCATION,
        ERROR_MULTIPLE_TEMPLATE_PARAM,
        FORMPART_WRONG_TYPE,
        INFO_PATH_INFO,
        INFO_PRODUCER_PROCESSING_DONE,
        INVALID_PORT,
        IS_MISSING,
        IS_NULL,
        MESSAGE_BODY_WRITER_NOT_FOUND,
        METHOD_MORE_THAN_ONE,
        NOT_ENOUGH_VALUES,
        NOT_EXIST,
        NOT_HAS_PATH_ANNOTATION,
        NULL_VALUE_NOT_ALLOWED,
        ONLY_POST_PUT_PATCH,
        PATH_TEMPLATE_INCORRECT,
        REPLACE_OPAQUE_URI,
        SET_OPAQUE_URI,
        SOMETHING_TOTALLY_WRONG,
        SSP_FRAGMENT,
        UNKNOWN_CONVERTER,
        VARIANT_LIST_ADD,
        WRONG_CONTENT_TYPE,
        WRONG_HTTP_STATUS_CODE,
        WRONG_TEMPLATE_PARAM,
        WRONG_TEMPLATE_PARAM_CHAR;
    }

    private Messages() {
    }

    public static String msg(Key key) {
        return MESSAGES.getString(key.name());
    }

    public static String msg(Key key, Object argument) {
        return MessageFormat.format(MESSAGES.getString(key.name()), argument);
    }

    public static String msg(Key key, Object argument1, Object arguments2) {
        return MessageFormat.format(MESSAGES.getString(key.name()), argument1, arguments2);
    }

    public static String msg(Key key, Object... arguments) {
        return MessageFormat.format(MESSAGES.getString(key.name()), arguments);
    }

    public static String isNull(Object argument) {
        return MessageFormat.format(MESSAGES.getString(Key.IS_NULL.name()), argument);
    }

    public static String isMissing(Object argument) {
        return MessageFormat.format(MESSAGES.getString(Key.IS_MISSING.name()), argument);
    }

    public static String notExist(Object argument) {
        return MessageFormat.format(MESSAGES.getString(Key.NOT_EXIST.name()), argument);
    }
}
