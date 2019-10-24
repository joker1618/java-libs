package xxx.joker.libs.datalayer.entities;

import xxx.joker.libs.datalayer.design.EntityField;
import xxx.joker.libs.datalayer.design.EntityPK;
import xxx.joker.libs.datalayer.design.RepoEntity;
import xxx.joker.libs.datalayer.design.ResourcePath;

import java.nio.file.Path;

public class RepoResource extends RepoEntity {

    @EntityField
    @ResourcePath
    private Path path;

    @EntityPK
    @EntityField
    private String name;
    @EntityPK
    @EntityField
    private RepoTags tags;
    @EntityField
    private String md5;
    @EntityField
    private RepoResourceType type;


    public RepoResource() {
    }

//    @Override
//    public String getPrimaryKey() {
//        return strf("{}-{}", getName(), getTags().format());
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RepoTags getTags() {
        return tags;
    }

    public void setTags(RepoTags tags) {
        this.tags = tags;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public RepoResourceType getType() {
        return type;
    }

    public void setType(RepoResourceType type) {
        this.type = type;
    }
}
