package xxx.joker.libs.datalayer.entities;

import xxx.joker.libs.core.format.JkFormattable;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.utils.JkStrings;

import java.util.*;

public class RepoTags implements JkFormattable<RepoTags> {

    private static final String SEP = "-";

    private final TreeSet<String> tags = new TreeSet<>(Comparator.comparing(String::toLowerCase));

    public RepoTags() {

    }
    public RepoTags(Collection<String> tags) {
        List<String> lowercase = JkStreams.map(tags, String::toLowerCase);
        this.tags.addAll(lowercase);
    }

    public boolean belongToGroup(RepoTags ot) {
        return JkStreams.filter(ot.tags, t -> !tags.contains(t)).isEmpty();
    }

    public static RepoTags of(String... tags) {
        List<String> tagList = JkStrings.splitFlat(tags);
        return new RepoTags(tagList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepoTags repoTags = (RepoTags) o;
        return Objects.equals(tags, repoTags.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tags);
    }

    @Override
    public String format() {
        return JkStreams.join(tags, SEP);
    }

    @Override
    public RepoTags parse(String str) {
        tags.clear();
        tags.addAll(JkStrings.splitList(str, SEP));
        return this;
    }
}
