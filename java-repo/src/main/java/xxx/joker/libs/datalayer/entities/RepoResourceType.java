package xxx.joker.libs.datalayer.entities;

import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.tests.JkTests;
import xxx.joker.libs.core.utils.JkConvert;

import java.nio.file.Path;
import java.util.List;

public enum RepoResourceType {

    IMAGE("gif", "jpeg", "jpg", "png", "tif", "tiff", "jif", "jfif"),
    MUSIC("mp3"),
    VIDEO("mp4", "avi", "mpeg", "mpg"),
    HTML("html"),
    TEXT("txt", "csv"),
    OTHER
    ;

    private List<String> extensions;

    RepoResourceType(String... extensions) {
        this.extensions = JkConvert.toList(extensions);
    }

    public static RepoResourceType fromExtension(Path path) {
        return fromExtension(JkFiles.getExtension(path));
    }
    public static RepoResourceType fromExtension(String extension) {
        if(!extension.isEmpty()) {
            for (RepoResourceType rut : values()) {
                if (JkTests.containsIgnoreCase(rut.extensions, extension)) {
                    return rut;
                }
            }
        }
        return OTHER;
    }

    public static RepoResourceType valueOfIgnoreCase(String name) {
        for (RepoResourceType rut : values()) {
            if(rut.name().equalsIgnoreCase(name)) {
                return rut;
            }
        }
        return null;
    }

}
