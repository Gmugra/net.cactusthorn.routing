package net.cactusthorn.routing.delegate;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

import net.cactusthorn.routing.util.Messages;

public final class MediaTypeHeaderDelegate implements HeaderDelegate<MediaType> {

    private static final ConcurrentHashMap<String, MediaType> CACHE = new ConcurrentHashMap<>();
    public static final int MAX_CACHE_SIZE = 250;

    @Override //
    public MediaType fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException(Messages.isNull("value"));
        }

        MediaType mediaType = CACHE.get(value);
        if (mediaType != null) {
            return mediaType;
        }

        mediaType = convert(value);
        if (CACHE.size() >= MAX_CACHE_SIZE) {
            CACHE.clear();
        }
        CACHE.put(value, mediaType);

        return mediaType;
    }

    private MediaType convert(String value) {
        int subtypeStart = value.indexOf('/');
        int parametersStart = value.indexOf(';');
        if (subtypeStart == -1 && parametersStart == -1) {
            return new MediaType(value.trim(), null);
        }
        if (parametersStart == -1) {
            return new MediaType(value.substring(0, subtypeStart).trim(), value.substring(subtypeStart + 1).trim());
        }
        String parametersPart = value.substring(parametersStart + 1);
        Map<String, String> parameters = new LinkedHashMap<>();
        String[] pairs = parametersPart.split(";");
        for (String pair : pairs) {
            addParameter(parameters, pair);
        }
        if (subtypeStart == -1) {
            return new MediaType(value.substring(0, parametersStart).trim(), null, parameters);
        }
        return new MediaType(value.substring(0, subtypeStart).trim(), value.substring(subtypeStart + 1, parametersStart).trim(),
                parameters);
    }

    @Override //
    public String toString(MediaType mediaType) {
        if (mediaType == null) {
            throw new IllegalArgumentException(Messages.isNull("mediaType"));
        }

        StringBuilder buf = new StringBuilder();
        buf.append(mediaType.getType()).append('/').append(mediaType.getSubtype());

        Map<String, String> parameters = mediaType.getParameters();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            buf.append(';').append(entry.getKey()).append('=');
            escapeIt(buf, entry.getValue());
        }
        return buf.toString();
    }

    private void escapeIt(StringBuilder buf, String value) {
        if (value.indexOf('\"') != -1) {
            buf.append('"');
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                if (c == '"') {
                    buf.append('\\');
                }
                buf.append(c);
            }
            buf.append('"');
        }
        buf.append(value);
    }

    private void addParameter(Map<String, String> parameters, String pair) {
        int valueStart = pair.indexOf('=');
        if (valueStart == -1) {
            throw new IllegalArgumentException(Messages.isMissing('='));
        }
        String value = pair.substring(valueStart + 1).trim();
        if (value.charAt(0) == '"') {
            value = value.replace("\\\"", "\"");
            value = value.substring(1, value.length() - 1).trim();
        }
        parameters.put(pair.substring(0, valueStart).trim(), value);
    }
}
