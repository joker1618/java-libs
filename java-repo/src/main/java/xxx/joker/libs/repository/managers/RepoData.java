package xxx.joker.libs.repository.managers;

import xxx.joker.libs.repository.design.JkEntity;

import java.util.*;

class RepoData {

    private TreeMap<Class<?>, Set<JkEntity>> dataSets;
    private List<ForeignKey> foreignKeys;

    public RepoData() {
        this.dataSets = new TreeMap<>(Comparator.comparing(Class::getName));
        this.foreignKeys = new ArrayList<>();
    }

    public TreeMap<Class<?>, Set<JkEntity>> getDataSets() {
        return dataSets;
    }
    public List<ForeignKey> getForeignKeys() {
        return foreignKeys;
    }
    public void setDataSets(TreeMap<Class<?>, Set<JkEntity>> dataSets) {
        this.dataSets = dataSets;
    }
    public void setForeignKeys(List<ForeignKey> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }
}
