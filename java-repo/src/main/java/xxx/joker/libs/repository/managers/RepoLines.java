package xxx.joker.libs.repository.managers;

import java.util.*;

class RepoLines {

    private TreeMap<Class<?>, List<String>> entityLines;
    private List<String> depsLines;

    public RepoLines() {
        this.entityLines = new TreeMap<>(Comparator.comparing(Class::getName));
        this.depsLines = new ArrayList<>();
    }

    public TreeMap<Class<?>, List<String>> getEntityLines() {
        return entityLines;
    }
    public List<String> getDepsLines() {
        return depsLines;
    }
}
