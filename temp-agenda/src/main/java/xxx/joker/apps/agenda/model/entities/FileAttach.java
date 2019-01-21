package xxx.joker.apps.agenda.model.entities;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.repository.design.JkEntityFieldCustom;

import java.nio.file.Path;
import java.nio.file.Paths;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class FileAttach implements JkEntityFieldCustom<FileAttach> {

    private String descr;
    private Path filePath;

    public FileAttach() {

    }

    public FileAttach(Path filePath) {
        this(filePath, "");
    }

    public FileAttach(Path filePath, String descr) {
        this.descr = descr;
        this.filePath = filePath;
    }

    @Override
    public String formatField() {
        return strf("{}:{}", filePath, descr);
    }

    @Override
    public void setFromString(String str) {
        if(StringUtils.isNotEmpty(str)) {
            int idx = str.indexOf(':');
            filePath = Paths.get(str.substring(0, idx));
            descr = str.substring(idx + 1);
        }
    }

    @Override
    public int compareTo(FileAttach o) {
        String ap = filePath.toAbsolutePath().normalize().toString();
        String bp = o.filePath.toAbsolutePath().normalize().toString();
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

    public Path getFilePath() {
        return filePath;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return formatField();
    }
}
