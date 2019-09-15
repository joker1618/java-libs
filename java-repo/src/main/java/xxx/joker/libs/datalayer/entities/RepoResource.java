package xxx.joker.libs.datalayer.entities;

import xxx.joker.libs.datalayer.design.RepoEntity;
import xxx.joker.libs.datalayer.design.RepoField;
import xxx.joker.libs.datalayer.design.RepoResourcePath;

import java.nio.file.Path;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class RepoResource extends RepoEntity {

    @RepoField
    @RepoResourcePath
    private Path path;

    @RepoField
    private String name;
    @RepoField
    private RepoTags tags;
    @RepoField
    private String md5;
    @RepoField
    private RepoResourceType type;


    public RepoResource() {
    }

    @Override
    public String getPrimaryKey() {
        return strf("{}-{}", getName(), getTags().format());
    }

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
