package xxx.joker.apps.agenda.model.entities;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.repository.design.JkEntityFieldCustom;

import java.nio.file.Path;
import java.nio.file.Paths;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class FileAttach implements JkEntityFieldCustom<FileAttach> {

    private String descr;
    private Path path;

    public FileAttach() {

    }

    public FileAttach(Path path) {
        this(path, "");
    }

    public FileAttach(Path path, String descr) {
        this.descr = descr;
        this.path = path;
    }

    @Override
    public String formatField() {
        return strf("{}:{}", path, descr);
    }

    @Override
    public void setFromString(String str) {
        if(StringUtils.isNotEmpty(str)) {
            int idx = str.indexOf(':');
            path = Paths.get(str.substring(0, idx));
            descr = str.substring(idx + 1);
        }
    }

    @Override
    public int compareTo(FileAttach o) {
        String ap = path.toAbsolutePath().normalize().toString();
        String bp = o.path.toAbsolutePath().normalize().toString();
        int res = ap.compareTo(bp);
        if(res == 0)   {
            res = descr.compareTo(o.descr);
        }
        return res;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return strf("{}: {}", descr, path.getFileName());
    }
}
