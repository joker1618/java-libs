package xxx.joker.libs.repository.entities;

import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoField;

import java.nio.file.Path;
import java.util.Set;

public class RepoUri extends RepoEntity {

    @RepoField
    private Path path;
    @RepoField
    private String md5;
    @RepoField
    private RepoMetaData repoMetaData;

    public RepoUri() {
    }


    @Override
    public String getPrimaryKey() {
        return md5;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
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

}
