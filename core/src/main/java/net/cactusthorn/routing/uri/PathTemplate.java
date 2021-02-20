package net.cactusthorn.routing.uri;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PathTemplate extends Template {

    public static final class PathValues {

        public static final PathValues EMPTY = new PathValues();

        private final Map<String, String> values = new HashMap<>();

        public PathValues() {
        }

        public PathValues(String name, String value) {
            values.put(name, value);
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

        i = o2.regExpParamsAmount() - o1.regExpParamsAmount();
        if (i != 0) {
            return i;
        }

        return 0;
    };

    private static final String SIMPLE_PATTERN = "([^/]+)";

    private static final String MULTIPLE_PARAM = "Template contain parameter \"%s\" multiple times with different pattern";

    private int simpleParamsAmount;
    private int regExpParamsAmount;

    private Pattern pattern;

    public PathTemplate(String template) {
        super(template);
        if (variables().isEmpty()) {
            pattern = Pattern.compile(template());
        } else {
            Map<String, String> paramsBuf = new HashMap<>();
            String patternTemplate = template();
            for (Template.TemplateVariable variable : variables()) {
                if (variable.pattern().isPresent()) {
                    String regExp = variable.pattern().get();
                    if (paramsBuf.containsKey(variable.name()) && !regExp.equals(paramsBuf.get(variable.name()))) {
                        throw new IllegalArgumentException(String.format(MULTIPLE_PARAM, variable.name()));
                    }
                    regExpParamsAmount++;
                    if (!paramsBuf.containsKey(variable.name())) {
                        patternTemplate = patternTemplate.replace(variable.template(), regExp);
                    }
                    paramsBuf.put(variable.name(), regExp);
                } else {
                    simpleParamsAmount++;
                    if (!paramsBuf.containsKey(variable.name())) {
                        patternTemplate = patternTemplate.replace(variable.template(), SIMPLE_PATTERN);
                    }
                    paramsBuf.put(variable.name(), SIMPLE_PATTERN);
                }
            }
            pattern = Pattern.compile(patternTemplate);
        }
    }

    public boolean match(String path) {
        if (variables().isEmpty()) {
            return template().equals(path);
        }
        Matcher matcher = pattern.matcher(path);
        return matcher.find();
    }

    public PathValues parse(String path) {
        if (variables().isEmpty()) {
            if (template().equals(path)) {
                return PathValues.EMPTY;
            }
            return null;
        }
        Matcher matcher = pattern.matcher(path);
        if (matcher.find()) {
            PathValues pathValues = new PathValues();
            for (int i = 0; i < variables().size(); i++) {
                pathValues.put(variables().get(i).name(), matcher.group(i + 1));
            }
            return pathValues;
        }
        return null;
    }

    public Pattern pattern() {
        return pattern;
    }

    public boolean isSimple() {
        return variables().isEmpty();
    }

    public int simpleParamsAmount() {
        return simpleParamsAmount;
    }

    public int regExpParamsAmount() {
        return regExpParamsAmount;
    }

    public int parametersAmount() {
        return simpleParamsAmount + regExpParamsAmount;
    }
}
