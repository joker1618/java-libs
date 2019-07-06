package xxx.joker.libs.repository.entities;

import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.tests.JkTests;
import xxx.joker.libs.core.utils.JkConvert;

import java.nio.file.Path;
import java.util.List;

public enum RepoUriType {

    IMAGE("gif", "jpeg", "jpg", "png", "tif", "tiff", "jif", "jfif"),
    MUSIC("mp3"),
    VIDEO("mp4", "avi", "mpeg", "mpg"),
    HTML("html"),
    TEXT("txt", "csv"),
    OTHER
    ;

    private List<String> extensions;

    RepoUriType(String... extensions) {
        this.extensions = JkConvert.toList(extensions);
    }

    public static RepoUriType fromExtension(Path path) {
        return fromExtension(path.toString());
    }
    public static RepoUriType fromExtension(String fname) {
        if(fname.contains(".")) {
            String ext = JkFiles.getExtension(fname);
            for (RepoUriType rut : values()) {
                if (JkTests.containsIgnoreCase(rut.extensions, ext)) {
                    return rut;
                }
            }
        }
        return OTHER;
    }

    public static RepoUriType valueOfIgnoreCase(String name) {
        for (RepoUriType rut : values()) {
            if(rut.name().equalsIgnoreCase(name)) {
                return rut;
            }
        }
        return null;
    }

}
