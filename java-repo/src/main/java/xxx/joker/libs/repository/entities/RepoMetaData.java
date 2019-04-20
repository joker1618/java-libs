package xxx.joker.libs.repository.entities;

import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.types.JkFormattable;
import xxx.joker.libs.core.utils.JkStrings;

import java.util.LinkedHashMap;
import java.util.Map;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class RepoMetaData implements JkFormattable {

    private static final String SEP_FIELD = "=";
    private static final String SEP_ELEMS = ";;";

    private Map<String, String> metadata = new LinkedHashMap<>();

    public enum Attrib {
        WIDTH,
        HEIGHT
    }

    public RepoMetaData() {

    }

    public void addMetaData(Attrib mdKey, String mdValue) {
        metadata.put(mdKey.name(), mdValue);
    }
    public void addMetaData(String mdKey, String mdValue) {
        metadata.put(mdKey,  mdValue);
    }


    @Override
    public String format() {
        return JkStreams.join(metadata.entrySet(), SEP_ELEMS,
                md -> strf("{}{}{}", md.getKey(), SEP_FIELD, md.getValue())
        );
    }

    @Override
    public String toString() {
        return format();
    }

    @Override
    public RepoMetaData parse(String str) {
        metadata.clear();
        for (String el : JkStrings.splitArr(str, SEP_ELEMS)) {
            String[] split = JkStrings.splitArr(el, SEP_FIELD);
            metadata.put(split[0], split[1]);
        }
        return this;
    }
}
