package net.cactusthorn.routing.bodywriter;

import javax.ws.rs.core.MediaType;
import java.util.Comparator;

public class MediaTypeAcceptComparator implements Comparator<MediaType> {

    @Override public int compare(MediaType o1, MediaType o2) {
        if (o1 == null && o2 == null) {
            return 0;
        }
        if (o1 == null) {
            return 1;
        }
        if (o2 == null) {
            return -1;
        }

        if (o1.isWildcardType() && !o2.isWildcardType()) {
            return 1;
        }
        if (!o1.isWildcardType() && o2.isWildcardType()) {
            return -1;
        }
        if (o1.isWildcardSubtype() && !o2.isWildcardSubtype()) {
            return 1;
        }
        if (!o1.isWildcardSubtype() && o2.isWildcardSubtype()) {
            return -1;
        }

        double q1 = getQ(o1);
        double q2 = getQ(o2);
        if (q1 > q2) {
            return -1;
        }
        if (q2 > q1) {
            return 1;
        }
        return 0;
    }

    private static double getQ(MediaType mediaType) {
        String q = mediaType.getParameters().get("q");
        if (q == null) {
            return 1d;
        }
        return Double.parseDouble(q);
    }
}
