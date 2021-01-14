package net.cactusthorn.routing.delegate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Iterator;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Variant;

public class VariantListBuilderImpl extends Variant.VariantListBuilder {

    private final List<Locale> llanguages = new ArrayList<>();
    private final List<String> eencodings = new ArrayList<>();
    private final List<MediaType> mmediaTypes = new ArrayList<>();
    private final List<Variant> variants = new ArrayList<>();

    private void reset() {
        mmediaTypes.clear();
        llanguages.clear();
        eencodings.clear();
    }

    @Override
    public Variant.VariantListBuilder add() {
        if (mmediaTypes.isEmpty() && llanguages.isEmpty() && eencodings.isEmpty()) {
            throw new UnsupportedOperationException("At least one media-type or language or encoding must be set");
        }

        Iterator<MediaType> mediaTypesIterator = mmediaTypes.iterator();
        do {
            MediaType mediaType = mediaTypesIterator.hasNext() ? mediaTypesIterator.next() : null;
            Iterator<Locale> languagesIterator = llanguages.iterator();
            do {
                Locale language = languagesIterator.hasNext() ? languagesIterator.next() : null;
                Iterator<String> encodingsIterator = eencodings.iterator();
                do {
                    String encoding = encodingsIterator.hasNext() ? encodingsIterator.next() : null;
                    variants.add(new Variant(mediaType, language, encoding));
                } while (encodingsIterator.hasNext());
            } while (languagesIterator.hasNext());
        } while (mediaTypesIterator.hasNext());

        reset();
        return this;
    }

    @Override
    public List<Variant> build() {
        if (!mmediaTypes.isEmpty() || !llanguages.isEmpty() || !eencodings.isEmpty()) {
            add();
        }
        List<Variant> clone = new ArrayList<>(variants);
        variants.clear();
        return clone;
    }

    @Override
    public Variant.VariantListBuilder encodings(String... encodings) {
        if (encodings != null) {
            Collections.addAll(this.eencodings, encodings);
        }
        return this;
    }

    @Override
    public Variant.VariantListBuilder languages(Locale... languages) {
        if (languages != null) {
            Collections.addAll(this.llanguages, languages);
        }
        return this;
    }

    @Override
    public Variant.VariantListBuilder mediaTypes(MediaType... mediaTypes) {
        if (mediaTypes != null) {
            Collections.addAll(this.mmediaTypes, mediaTypes);
        }
        return this;
    }

}
