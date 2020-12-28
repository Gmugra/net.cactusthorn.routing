package net.cactusthorn.routing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.Cookie;

public final class Response {

    private int statusCode;
    private Object body;
    private String contentType;
    private String template;
    private String characterEncoding;
    private boolean skipProducer;
    private List<Cookie> cookies;
    private Map<String, List<String>> headers;
    private Map<String, List<Integer>> intHeaders = new HashMap<>();
    private Map<String, List<Long>> dateHeaders = new HashMap<>();

    // @formatter:off
    private Response(
                int statusCode,
                Object body,
                String contentType,
                String template,
                String characterEncoding,
                boolean skipProducer,
                List<Cookie> cookies,
                Map<String, List<String>> headers,
                Map<String, List<Integer>> intHeaders,
                Map<String, List<Long>> dateHeaders) {
        this.statusCode = statusCode;
        this.body = body;
        this.contentType = contentType;
        this.template = template;
        this.characterEncoding = characterEncoding;
        this.skipProducer = skipProducer;
        this.cookies = cookies;
        this.headers = headers;
        this.intHeaders = intHeaders;
        this.dateHeaders = dateHeaders;
    }
    // @formatter:on

    public static Builder builder() {
        return new Builder();
    }

    public int statusCode() {
        return statusCode;
    }

    public Object body() {
        return body;
    }

    public Optional<String> contentType() {
        return Optional.ofNullable(contentType);
    }

    public Optional<String> template() {
        return Optional.ofNullable(template);
    }

    public Optional<String> characterEncoding() {
        return Optional.ofNullable(characterEncoding);
    }

    public boolean skipProducer() {
        return skipProducer;
    }

    public List<Cookie> cookies() {
        return cookies;
    }

    public Map<String, List<String>> headers() {
        return headers;
    }

    public Map<String, List<Integer>> intHeaders() {
        return intHeaders;
    }

    public Map<String, List<Long>> dateHeaders() {
        return dateHeaders;
    }

    public static final class Builder {

        private static final int DEFAULT_STATUS_CODE = 200;

        private int statuscode = DEFAULT_STATUS_CODE;
        private Object bbody;
        private String contenttype;
        private String ttemplate;
        private String characterencoding;
        private final List<Cookie> cookies = new ArrayList<>();
        private final Map<String, List<String>> headers = new HashMap<>();
        private final Map<String, List<Integer>> intHeaders = new HashMap<>();
        private final Map<String, List<Long>> dateHeaders = new HashMap<>();
        private boolean skipProducer;

        private Builder() {
        }

        /**
         * Sets the status code for this response.
         *
         * @param statusCode the status code
         *
         * @return the updated builder
         */
        public Builder setStatus(int statusCode) {
            statuscode = statusCode;
            return this;
        }

        public Builder setBody(Object body) {
            bbody = body;
            return this;
        }

        public Builder setContentType(String contentType) {
            contenttype = contentType;
            return this;
        }

        public Builder setTemplate(String template) {
            ttemplate = template;
            return this;
        }

        public Builder setCharacterEncoding(String characterEncoding) {
            characterencoding = characterEncoding;
            return this;
        }

        public Builder skipProducer() {
            skipProducer = true;
            return this;
        }

        /**
         * Adds the specified cookie to the response. This method can be called multiple
         * times to set more than one cookie.
         *
         * @param cookie the Cookie to return to the client
         *
         * @return the updated builder
         */
        public Builder addCookie(Cookie cookie) {
            cookies.add(cookie);
            return this;
        }

        /**
         *
         * Sets a response header with the given name and value. If the header had
         * already been set, the new value overwrites the previous one.
         *
         * @param name  the name of the header
         * @param value the header value If it contains octet string, it should be
         *              encoded according to RFC 2047
         *              (http://www.ietf.org/rfc/rfc2047.txt)
         *
         * @return the updated builder
         */
        public Builder setHeader(String name, String value) {
            List<String> list = new ArrayList<>();
            list.add(value);
            headers.put(name, list);
            return this;
        }

        /**
         * Adds a response header with the given name and value. This method allows
         * response headers to have multiple values.
         *
         * @param name  the name of the header
         * @param value the additional header value If it contains octet string, it
         *              should be encoded according to RFC 2047
         *              (http://www.ietf.org/rfc/rfc2047.txt)
         *
         * @return the updated builder
         *
         * @see #setHeader
         */
        public Builder addHeader(String name, String value) {
            if (headers.containsKey(name)) {
                headers.get(name).add(value);
            } else {
                List<String> list = new ArrayList<>();
                list.add(value);
                headers.put(name, list);
            }
            return this;
        }

        /**
         * Sets a response header with the given name and integer value. If the header
         * had already been set, the new value overwrites the previous one.
         *
         * @param name  the name of the header
         * @param value the assigned integer value
         *
         * @return the updated builder
         */
        public Builder setIntHeader(String name, int value) {
            List<Integer> list = new ArrayList<>();
            list.add(value);
            intHeaders.put(name, list);
            return this;
        }

        /**
         * Adds a response header with the given name and integer value. This method
         * allows response headers to have multiple values.
         *
         * @param name  the name of the header
         * @param value the assigned integer value
         *
         * @return the updated builder
         *
         * @see #setIntHeader
         */
        public Builder addIntHeader(String name, int value) {
            if (intHeaders.containsKey(name)) {
                intHeaders.get(name).add(value);
            } else {
                List<Integer> list = new ArrayList<>();
                list.add(value);
                intHeaders.put(name, list);
            }
            return this;
        }

        /**
         *
         * Sets a response header with the given name and date-value. The date is
         * specified in terms of milliseconds since the epoch. If the header had already
         * been set, the new value overwrites the previous one.
         *
         * @param name the name of the header to set
         * @param date the assigned date value
         *
         * @return the updated builder
         */
        public Builder setDateHeader(String name, long date) {
            List<Long> list = new ArrayList<>();
            list.add(date);
            dateHeaders.put(name, list);
            return this;
        }

        /**
         *
         * Adds a response header with the given name and date-value. The date is
         * specified in terms of milliseconds since the epoch. This method allows
         * response headers to have multiple values.
         *
         * @param name the name of the header to set
         * @param date the additional date value
         *
         * @return the updated builder
         *
         * @see #setDateHeader
         */
        public Builder addDateHeader(String name, long date) {
            if (dateHeaders.containsKey(name)) {
                dateHeaders.get(name).add(date);
            } else {
                List<Long> list = new ArrayList<>();
                list.add(date);
                dateHeaders.put(name, list);
            }
            return this;
        }

        public Response build() {
            List<Cookie> unmodifiableCookies = Collections.unmodifiableList(cookies);
            Map<String, List<String>> unmodifiableHeaders = Collections.unmodifiableMap(headers);
            Map<String, List<Integer>> unmodifiableIntHeaders = Collections.unmodifiableMap(intHeaders);
            Map<String, List<Long>> unmodifiableDateHeaders = Collections.unmodifiableMap(dateHeaders);
            return new Response(statuscode, bbody, contenttype, ttemplate, characterencoding, skipProducer, unmodifiableCookies,
                    unmodifiableHeaders, unmodifiableIntHeaders, unmodifiableDateHeaders);
        }
    }

}
