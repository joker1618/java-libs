package xxx.joker.libs.repository.entities;

import xxx.joker.libs.core.media.JkImage;
import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoField;
import xxx.joker.libs.repository.exceptions.RepoError;

import java.nio.file.Path;

public class RepoUri extends RepoEntity {

    @RepoField
    private Path path;
    @RepoField
    private String md5;
    @RepoField
    private RepoUriType type;
    @RepoField
    private RepoMetaData metaData;

    public RepoUri() {
    }


    @Override
    public String getPrimaryKey() {
        return "uri:" + getMd5();
    }

    public JkImage toJkImage() {
        if(type != RepoUriType.IMAGE)   return null;
        JkImage img = new JkImage();
        img.setPath(getPath());
        img.setWidth(Integer.parseInt(getMetaData().get(RepoMetaData.Attrib.WIDTH)));
        img.setHeight(Integer.parseInt(getMetaData().get(RepoMetaData.Attrib.HEIGHT)));
        return img;
    }


    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public RepoMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(RepoMetaData metaData) {
        this.metaData = metaData;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public RepoUriType getType() {
        return type;
    }

    public void setType(RepoUriType type) {
        this.type = type;
    }
}
