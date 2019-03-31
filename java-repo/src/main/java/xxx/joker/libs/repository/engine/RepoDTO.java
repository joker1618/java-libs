package xxx.joker.libs.repository.engine;

import xxx.joker.libs.repository.design.RepoEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class RepoDTO {

    private Class<?> eClazz;
    private List<RepoEntity> entities;
    private List<RepoFK> foreignKeys;

    public RepoDTO(Class<?> eClazz) {
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

    public List<RepoFK> getForeignKeys() {
        return foreignKeys;
    }

    public void setForeignKeys(List<RepoFK> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }
}
