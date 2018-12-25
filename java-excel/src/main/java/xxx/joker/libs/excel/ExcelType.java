package xxx.joker.libs.excel;

import xxx.joker.libs.core.utils.JkFiles;

import java.nio.file.Path;

public enum ExcelType {

    HSSF("xls"),
    XSSF("xlsx")
    ;

    private String extension;

    ExcelType(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public static ExcelType fromExtension(Path path) {
        return fromExtension(JkFiles.getExtension(path));
    }
    public static ExcelType fromExtension(String extension) {
        for(ExcelType et : values()) {
            if(et.extension.equalsIgnoreCase(extension)) {
                return et;
            }
        }
        return null;
    }
}
