package xxx.joker.libs.datalayer.entities;

import xxx.joker.libs.core.media.JkImage;
import xxx.joker.libs.datalayer.design.RepoEntity;
import xxx.joker.libs.datalayer.design.RepoField;

import java.nio.file.Path;

import static xxx.joker.libs.core.utils.JkStrings.strf;
import static xxx.joker.libs.datalayer.entities.RepoMetaData.Attrib;

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
        return strf("uri:{}", path);
    }

    public JkImage toJkImage() {
        if(type != RepoUriType.IMAGE)   return null;
        JkImage img = new JkImage();
        img.setPath(getPath());
        img.setWidth(getMetaData().getInt(Attrib.WIDTH));
        img.setHeight(getMetaData().getInt(Attrib.HEIGHT));
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
