package xxx.joker.apps.formula1.common;

import xxx.joker.libs.core.runtimes.JkEnvironment;

import java.nio.file.Path;

public class F1Const {

    public static final Path BASE_FOLDER = JkEnvironment.getAppsFolder().resolve("formula1");

    public static final Path HTML_FOLDER = BASE_FOLDER.resolve("html");

    public static final Path DB_FOLDER = BASE_FOLDER.resolve("repository");
    public static final String DB_NAME = "f1";

    public static final Path IMAGES_FOLDER = BASE_FOLDER.resolve("images");
    public static final Path FLAGS_FOLDER = IMAGES_FOLDER.resolve("flags");
    public static final Path DRIVER_PIC_FOLDER = IMAGES_FOLDER.resolve("drivers");
    public static final Path TRACK_MAP_FOLDER = IMAGES_FOLDER.resolve("trackMap");

}
