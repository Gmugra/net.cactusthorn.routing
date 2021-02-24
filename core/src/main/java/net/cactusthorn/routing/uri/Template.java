package net.cactusthorn.routing.uri;

import static java.text.CharacterIterator.DONE;

import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.cactusthorn.routing.util.Messages;
import static net.cactusthorn.routing.util.Messages.Key.WRONG_TEMPLATE_PARAM;
import static net.cactusthorn.routing.util.Messages.Key.WRONG_TEMPLATE_PARAM_CHAR;

public class Template {

    public static final class TemplateVariable {
        private String name;
        private String template;
        private Optional<String> pattern;

        private TemplateVariable(String name) {
            this(name, null);
        }

        private TemplateVariable(String name, String pattern) {
            this.name = name;
            this.template = '{' + name + '}';
            if (pattern == null) {
                this.pattern = Optional.empty();
            } else {
                this.pattern = Optional.of('(' + pattern + ')');
            }
        }

        public String name() {
            return name;
        }

        public String template() {
            return template;
        }

        public Optional<String> pattern() {
            return pattern;
        }

        @Override public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof TemplateVariable)) {
                return false;
            }
            TemplateVariable v = (TemplateVariable) obj;
            if (!name.equals(v.name())) {
                return false;
            }
            if (!pattern.equals(v.pattern())) {
                return false;
            }
            return true;
        }

        @Override public int hashCode() {
            return name.hashCode() + pattern.hashCode();
        }
    }

    private final List<TemplateVariable> variables = new ArrayList<>();
    private String template;
    private int literalCharsAmount;

    public Template(String template) {
        if (template == null) {
            throw new IllegalArgumentException("Template is null");
        }
        String prepared = template.trim();
        if (prepared.isEmpty()) {
            throw new IllegalArgumentException("Template is empty");
        }
        process(prepared);
    }

    public String template() {
        return template;
    }

    public List<TemplateVariable> variables() {
        return variables;
    }

    public int literalCharsAmount() {
        return literalCharsAmount;
    }

    private void process(String prepared) {

        StringBuilder templateBuf = new StringBuilder();

        StringCharacterIterator it = new StringCharacterIterator(prepared);
        for (char c = it.first(); c != DONE; c = it.next()) {
            if (c == '{') {
                TemplateVariable variable = processParam(it);
                variables.add(variable);
                templateBuf.append(variable.template());
            } else {
                literalCharsAmount++;
                templateBuf.append(c);
            }
        }

        template = templateBuf.toString();
    }

    private TemplateVariable processParam(StringCharacterIterator it) {

        StringBuilder param = new StringBuilder();
        int lastSpaceIndex = 0;

        char c = eraseWhitespaces(it);
        do {
            if (c == '{') {
                throw new IllegalArgumentException(Messages.msg(WRONG_TEMPLATE_PARAM));
            }
            if (c == '}') {
                return new TemplateVariable(param.toString());
            }
            if (c == ':') {
                return new TemplateVariable(param.toString(), processRegExp(it));
            }
            if (Character.isLetterOrDigit(c) || c == '_') {
                if (lastSpaceIndex == 0) {
                    param.append(c);
                } else {
                    throw new IllegalArgumentException(Messages.msg(WRONG_TEMPLATE_PARAM_CHAR, lastSpaceIndex + 1));
                }
            } else if (Character.isWhitespace(c)) {
                lastSpaceIndex = it.getIndex();
            } else {
                throw new IllegalArgumentException(Messages.msg(WRONG_TEMPLATE_PARAM_CHAR, it.getIndex() + 1));
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

        throw new IllegalArgumentException(Messages.msg(WRONG_TEMPLATE_PARAM));
    }

    @Override //
    public int hashCode() {
        return template.hashCode();
    }

    @Override //
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Template)) {
            return false;
        }
        Template t = (Template) obj;
        if (!template.equals(t.template())) {
            return false;
        }
        return variables.equals(t.variables());
    }
}
