package trymodel.entities2;

import xxx.joker.libs.repository.design.JkEntityField;
import xxx.joker.libs.repository.design.JkRepoEntity;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class CustomAndCollections extends JkRepoEntity {

    @JkEntityField(idx = 0)
    private String keyword;
    @JkEntityField(idx = 1)
    private CustomEntityJk customEntity;
    @JkEntityField(idx = 2)
    private List<Integer> listInt;
    @JkEntityField(idx = 3)
    private Set<Path> setPath;
    @JkEntityField(idx = 4)
    private List<CustomEntityJk> ceList;

    public CustomAndCollections(){}
    public CustomAndCollections(String keyword, CustomEntityJk customEntity, List<Integer> listInt, Set<Path> setPath, List<CustomEntityJk> ceList) {
        this.keyword = keyword;
        this.customEntity = customEntity;
        this.listInt = listInt;
        this.setPath = setPath;
        this.ceList = ceList;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public CustomEntityJk getCustomEntity() {
        return customEntity;
    }

    public void setCustomEntity(CustomEntityJk customEntity) {
        this.customEntity = customEntity;
    }

    public List<Integer> getListInt() {
        return listInt;
    }

    public void setListInt(List<Integer> listInt) {
        this.listInt = listInt;
    }

    public Set<Path> getSetPath() {
        return setPath;
    }

    public void setSetPath(Set<Path> setPath) {
        this.setPath = setPath;
    }

    public List<CustomEntityJk> getCeList() {
        return ceList;
    }

    public void setCeList(List<CustomEntityJk> ceList) {
        this.ceList = ceList;
    }

    @Override
    public String getPrimaryKey() {
        return getKeyword();
    }
}
