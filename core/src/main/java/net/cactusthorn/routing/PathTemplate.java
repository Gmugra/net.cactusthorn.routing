package net.cactusthorn.routing;

import static java.text.CharacterIterator.DONE;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.StringCharacterIterator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PathTemplate {

    public static final class PathValues {

        public static final PathValues EMPTY = new PathValues();

        private final Map<String, String> values = new HashMap<>();

        public PathValues() {
        }

        public void put(String name, String value) {
            values.put(name, value);
        }

        public String value(String name) {
            return values.get(name);
        }
    }

    public static final Comparator<PathTemplate> COMPARATOR = (o1, o2) -> {
        if (o1 == null && o2 == null) {
            return 0;
        }
        if (o1 == null) {
            return 1;
        }
        if (o2 == null) {
            return -1;
        }

        int i = o2.literalCharsAmount() - o1.literalCharsAmount();
        if (i != 0) {
            return i;
        }

        i = o2.parametersAmount() - o1.parametersAmount();
        if (i != 0) {
            return i;
        }

        i = o2.regExpParametersAmount() - o1.regExpParametersAmount();
        if (i != 0) {
            return i;
        }

        return 0;
    };

    private String literal;
    private int literalCharsAmount;

    private Pattern pattern;

    private final ArrayList<String> parameters = new ArrayList<>();

    private boolean simple;
    private int simpleParamsAmount;
    private int regExpParamsAmount;

    public PathTemplate(String template) {
        if (template == null) {
            throw new IllegalArgumentException("Template is null");
        }
        String prepared = template.trim();
        if (prepared.isEmpty()) {
            throw new IllegalArgumentException("Template is empty");
        }
        process(prepared);
    }

    public static final String SIMPLE_PATTERN = "[^/]+";

    private void process(String prepared) {

        StringBuilder literalBuf = new StringBuilder();
        StringBuilder patternBuf = new StringBuilder();
        Map<String, String> paramsBuf = new HashMap<>();

        StringCharacterIterator it = new StringCharacterIterator(prepared);
        for (char c = it.first(); c != DONE; c = it.next()) {
            if (c == '{') {
                String paramPattern = processParam(it, paramsBuf);
                patternBuf.append('(').append(paramPattern).append(')');
            } else {
                patternBuf.append(c);
                literalBuf.append(c);
            }
        }

        literal = literalBuf.toString();
        literalCharsAmount = literalBuf.length();

        simple = parameters.isEmpty();

        pattern = Pattern.compile(patternBuf.toString());

        // It must be valid URI after cut all parameters
        // TODO not the best way: check only valid for URI characters before
        try {
            new URI(literal);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Template is invalid URI", e);
        }
    }

    private static final String WRONG_PARAM = "Template contain improperly closed parameter";
    private static final String WRONG_CHAR = "Template contain wrong character for parameter name at the possition %d";
    private static final String MULTIPLE_PARAM = "Template contain parameter \"%s\" multiple times with different pattern";

    private String processParam(StringCharacterIterator it, Map<String, String> paramsBuf) {

        StringBuilder param = new StringBuilder();
        int lastSpaceIndex = 0;

        char c = eraseWhitespaces(it);
        do {
            if (c == '{') {
                throw new IllegalArgumentException(WRONG_PARAM);
            }
            if (c == '}') {
                String paramName = param.toString();
                paramsBuf.put(paramName, SIMPLE_PATTERN);
                parameters.add(param.toString());
                simpleParamsAmount++;
                return SIMPLE_PATTERN;
            }
            if (c == ':') {
                String regExp = processRegExp(it);
                String paramName = param.toString();
                if (paramsBuf.containsKey(paramName) && !regExp.equals(paramsBuf.get(paramName))) {
                    throw new IllegalArgumentException(String.format(MULTIPLE_PARAM, paramName));
                }
                parameters.add(param.toString());
                regExpParamsAmount++;
                return regExp;
            }
            if (Character.isLetterOrDigit(c) || c == '_') {
                if (lastSpaceIndex == 0) {
                    param.append(c);
                } else {
                    throw new IllegalArgumentException(String.format(WRONG_CHAR, lastSpaceIndex + 1));
                }
            } else if (Character.isWhitespace(c)) {
                lastSpaceIndex = it.getIndex();
            } else {
                throw new IllegalArgumentException(String.format(WRONG_CHAR, it.getIndex() + 1));
            }
            c = it.next();
        } while (true);
    }

    private char eraseWhitespaces(StringCharacterIterator it) {
        while (true) {
            char c = it.next();
            if (!Character.isWhitespace(c)) {
                return c;
            }
        }
    }

    private String processRegExp(StringCharacterIterator it) {

        int opened = 1;
        StringBuilder regExp = new StringBuilder();

        char c = eraseWhitespaces(it);
        do {
            if (c == '{') {
                opened++;
            } else if (c == '}') {
                opened--;
                if (opened == 0) {
                    return regExp.toString().trim();
                }
            }
            regExp.append(c);
            c = it.next();
        } while (c != DONE);

        throw new IllegalArgumentException(WRONG_PARAM);
    }

    public boolean match(String path) {
        if (simple) {
            return literal.equals(path);
        }
        Matcher matcher = pattern.matcher(path);
        return matcher.find();
    }

    public PathValues parse(String path) {
        if (simple) {
            if (literal.equals(path)) {
                return PathValues.EMPTY;
            }
            return null;
        }
        Matcher matcher = pattern.matcher(path);
        if (matcher.find()) {
            PathValues pathValues = new PathValues();
            for (int i = 0; i < parameters.size(); i++) {
                pathValues.put(parameters.get(i), matcher.group(i + 1));
            }
            return pathValues;
        }
        return null;
    }

    public Pattern pattern() {
        return pattern;
    }

    public List<String> parameters() {
        return parameters;
    }

    public int literalCharsAmount() {
        return literalCharsAmount;
    }

    public boolean isSimple() {
        return simple;
    }

    public int simpleParametersAmount() {
        return simpleParamsAmount;
    }

    public int regExpParametersAmount() {
        return regExpParamsAmount;
    }

    public int parametersAmount() {
        return simpleParamsAmount + regExpParamsAmount;
    }

    @Override //
    public int hashCode() {
        return pattern.pattern().hashCode();
    }

    @Override //
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PathTemplate)) {
            return false;
        }
        PathTemplate template = (PathTemplate) obj;
        if (!pattern.pattern().equals(template.pattern().pattern())) {
            return false;
        }
        return parameters.equals(template.parameters);
    }
}
