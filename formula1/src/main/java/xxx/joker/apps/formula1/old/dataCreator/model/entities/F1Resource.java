package xxx.joker.apps.formula1.old.dataCreator.model.entities;

import xxx.joker.apps.formula1.old.fxlibs.JxImage;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoField;

import java.nio.file.Path;
import java.util.List;

public class F1Resource extends RepoEntity {

    @RepoField
    private String key;
    @RepoField
    private Path path;
    @RepoField
    private int width;
    @RepoField
    private int height;
    @RepoField
    private List<String> tags;

    public F1Resource() {
    }
    public F1Resource(Path path) {
        this.path = path;
    }
    public F1Resource(JxImage image) {
        this.path = image.getPath();
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    public JxImage convertToImage() {
        JxImage img = JxImage.parse(path);
        img.setDescription(getPrimaryKey());
        return img;
    }

    @Override
    public String getPrimaryKey() {
        return "resource-" + path.toString();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(String... tags) {
        this.tags = JkConvert.toList(tags);
    }
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
