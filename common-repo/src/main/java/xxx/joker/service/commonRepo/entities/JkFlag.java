package xxx.joker.service.commonRepo.entities;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.media.JkImage;
import xxx.joker.libs.core.media.JkMedia;
import xxx.joker.libs.core.types.JkFormattable;
import xxx.joker.libs.core.utils.JkStrings;
import xxx.joker.libs.repository.design.RepoEntity;
import xxx.joker.libs.repository.design.RepoField;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class JkFlag implements JkFormattable {

    private static final String SEP = "#";

    private JkImage icon;
    private JkImage image;


    @Override
    public String toString() {
        return format();
    }

    @Override
    public String format() {
        String str = "";
        str += icon.format();
        str += SEP;
        str += image.format();
        return str;
    }

    @Override
    public JkFlag parse(String str) {
        String[] split = JkStrings.splitArr(str, SEP);
        icon = new JkImage().parse(split[0]);
        image = new JkImage().parse(split[1]);
        return this;
    }

    public JkImage getIcon() {
        return icon;
    }

    public void setIcon(JkImage icon) {
        this.icon = icon;
    }

    public JkImage getImage() {
        return image;
    }

    public void setImage(JkImage image) {
        this.image = image;
    }
}
