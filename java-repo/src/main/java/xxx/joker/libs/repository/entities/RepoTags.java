package xxx.joker.libs.repository.entities;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.tests.JkTests;
import xxx.joker.libs.core.types.JkFormattable;
import xxx.joker.libs.core.utils.JkStrings;

import java.util.*;

public class RepoTags implements JkFormattable<RepoTags> {

    private static final String SEP = "-";

    private TreeSet<String> tags = new TreeSet<>(Comparator.comparing(String::toLowerCase));

    public RepoTags() {

    }
    public RepoTags(Collection<String> tags) {
        this.tags.addAll(tags);
    }

    public static RepoTags of(String... tags) {
        List<String> tagList = new ArrayList<>();
        for (String tag : tags) {
            List<String> tlist = JkStrings.splitList(tag.replaceAll("[\\s,;|]", ","), ",", true);
            tlist.removeIf(StringUtils::isBlank);
            tagList.addAll(tlist);
        }
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
