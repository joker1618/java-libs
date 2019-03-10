package xxx.joker.libs.repository.managers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

class RepoLines {

    private TreeMap<Class<?>, List<String>> entityLines;
    private List<String> fkLines;

    public RepoLines() {
        this.entityLines = new TreeMap<>(Comparator.comparing(Class::getName));
        this.fkLines = new ArrayList<>();
    }

    public TreeMap<Class<?>, List<String>> getEntityLines() {
        return entityLines;
    }
    public List<String> getFkLines() {
        return fkLines;
    }
}
