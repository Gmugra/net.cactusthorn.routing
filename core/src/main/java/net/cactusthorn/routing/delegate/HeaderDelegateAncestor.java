package net.cactusthorn.routing.delegate;

public class HeaderDelegateAncestor {

    protected boolean containsWhiteSpace(String str) {
        for (char c : str.toCharArray()) {
            if (Character.isWhitespace(c)) {
                return true;
            }
        }
        return false;
    }

    protected String addQuotesIfContainsWhitespace(String str) {
        if (containsWhiteSpace(str)) {
            return '"' + str + '"';
        }
        return str;
    }
}
