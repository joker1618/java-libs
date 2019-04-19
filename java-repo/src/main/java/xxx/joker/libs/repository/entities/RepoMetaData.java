package xxx.joker.libs.repository.entities;

import org.apache.commons.lang3.tuple.Pair;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.types.JkFormattable;
import xxx.joker.libs.core.utils.JkStrings;

import java.util.ArrayList;
import java.util.List;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class RepoMetaData implements JkFormattable {

    private static final String SEP_FIELD = "=";
    private static final String SEP_ELEMS = ";;";

    private List<Pair<String, String>> metadata = new ArrayList<>();

    public RepoMetaData() {

    }

    public void addMetaData(String key, String value) {
        metadata.add(Pair.of(key, value));
    }

    @Override
    public String format() {
        return JkStreams.join(metadata, SEP_ELEMS, md -> strf("{}{}{}", md.getKey(), SEP_FIELD, md.getValue()));
    }

    @Override
    public String toString() {
        return format();
    }

    @Override
    public RepoMetaData parse(String str) {
        for (String el : JkStrings.splitArr(str, SEP_ELEMS)) {
            String[] split = JkStrings.splitArr(el, SEP_FIELD);
            metadata.add(Pair.of(split[0], split[1]));
        }
        return this;
    }
}
