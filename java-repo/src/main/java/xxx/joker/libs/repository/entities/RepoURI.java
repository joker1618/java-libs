package xxx.joker.libs.repository.entities;

import xxx.joker.libs.core.runtimes.JkEnvironment;
import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoField;

import java.nio.file.Path;
import java.util.Set;

public class RepoURI extends RepoEntity {

    @RepoField
    private Path path;
    @RepoField
    private String md5;
    @RepoField
    private String name;
    @RepoField
    private RepoMetaData repoMetaData;
    @RepoField
    private Set<String> tags;

    public RepoURI() {
    }
    public RepoURI(Path path) {
        this.path = path;
    }

    @Override
    public String getPrimaryKey() {
        return JkEnvironment.relativizeAppsPath(path).toString();
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RepoMetaData getRepoMetaData() {
        return repoMetaData;
    }

    public void setRepoMetaData(RepoMetaData repoMetaData) {
        this.repoMetaData = repoMetaData;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
