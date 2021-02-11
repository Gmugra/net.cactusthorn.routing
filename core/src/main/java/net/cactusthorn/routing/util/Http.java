package net.cactusthorn.routing.util;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.RuntimeDelegate;
import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

public final class Http {

    @SuppressWarnings("unchecked") //
    public static void writeHeaders(HttpServletResponse response, MultivaluedMap<String, Object> headers) {
        for (Map.Entry<String, List<Object>> entry : headers.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            String name = entry.getKey();
            for (Object header : entry.getValue()) {
                if (header == null) {
                    continue;
                }
                @SuppressWarnings("rawtypes") //
                HeaderDelegate headerDelegate = RuntimeDelegate.getInstance().createHeaderDelegate(header.getClass());
                if (headerDelegate != null) {
                    response.addHeader(name, headerDelegate.toString(header));
                } else {
                    response.addHeader(name, header.toString());
                }
            }
        }
    }
}
