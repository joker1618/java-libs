package xxx.joker.libs.repository.znew;

import xxx.joker.libs.repository.design2.RepoEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

class X_RepoEntityDTO {

    private Class<?> eClazz;
    private List<RepoEntity> entities;
    private List<X_RepoFK> foreignKeys;

    public X_RepoEntityDTO(Class<?> eClazz) {
        this.eClazz = eClazz;
        this.entities = new ArrayList<>();
        this.foreignKeys = new ArrayList<>();
    }

    public Class<?> getEClazz() {
        return eClazz;
    }

    public List<RepoEntity> getEntities() {
        return entities;
    }

    public void setEntities(Collection<RepoEntity> entities) {
        this.entities = new ArrayList<>(entities);
    }

    public List<X_RepoFK> getForeignKeys() {
        return foreignKeys;
    }

    public void setForeignKeys(List<X_RepoFK> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }
}
