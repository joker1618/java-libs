package xxx.joker.libs.datalayer.dao;

import xxx.joker.libs.datalayer.design.RepoEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DaoDto {

    private Class<?> eClazz;
    private List<RepoEntity> entities;
    private List<DaoFK> foreignKeys;

    public DaoDto(Class<?> eClazz) {
        this.eClazz = eClazz;
        this.entities = new ArrayList<>();
        this.foreignKeys = new ArrayList<>();
    }

    public Class<?> getEClazz() {
        return eClazz;
    }

    public void setEClazz(Class<?> eClazz) {
        this.eClazz = eClazz;
    }

    public List<RepoEntity> getEntities() {
        return entities;
    }

    public void setEntities(Collection<RepoEntity> entities) {
        this.entities = new ArrayList<>(entities);
    }

    public List<DaoFK> getForeignKeys() {
        return foreignKeys;
    }

    public void setForeignKeys(List<DaoFK> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }
}
