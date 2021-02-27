package net.cactusthorn.routing.util;

import java.util.Comparator;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;

public class Prioritised {

    public static final int LOWEST_PRIORITY = 9999;
    public static final int PRIORITY_HIGHEST = 50;

    public static final Comparator<Prioritised> PRIORITY_COMPARATOR = (p1, p2) -> {
        if (p1 == null && p2 == null) {
            return 0;
        }
        if (p1 == null) {
            return 1;
        }
        if (p2 == null) {
            return -1;
        }
        return p1.priority() - p2.priority();
    };

    private final int priority;

    public Prioritised(Class<?> clazz) {
        Priority annotation = clazz.getAnnotation(Priority.class);
        if (annotation != null) {
            priority = annotation.value();
        } else {
            priority = Priorities.USER;
        }
    }

    public int priority() {
        return priority;
    }
}
