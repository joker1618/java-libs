package xxx.joker.libs.excel;

import xxx.joker.libs.core.file.JkFiles;

import java.nio.file.Path;

public enum JkExcelType {

    HSSF("xls"),
    XSSF("xlsx")
    ;

    private String extension;

    JkExcelType(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public static JkExcelType fromExtension(Path path) {
        return fromExtension(JkFiles.getExtension(path));
    }
    public static JkExcelType fromExtension(String extension) {
        for(JkExcelType et : values()) {
            if(et.extension.equalsIgnoreCase(extension)) {
                return et;
            }
        }
        return null;
    }
}
