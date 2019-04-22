package xxx.joker.apps.formula1.old.common;

import xxx.joker.libs.core.runtimes.JkEnvironment;

import java.nio.file.Path;

public class F1Const {

    public static final Path BASE_FOLDER = JkEnvironment.getAppsFolder().resolve("formula1");

    public static final Path HTML_FOLDER = BASE_FOLDER.resolve("html");

    public static final Path DB_FOLDER = BASE_FOLDER.resolve("repository");
    public static final String DB_NAME = "f1";

    public static final Path IMG_FOLDER = BASE_FOLDER.resolve("images");
    public static final Path IMG_FLAGS_ICON_FOLDER = IMG_FOLDER.resolve("icons/flags");
    public static final Path IMG_FLAGS_FOLDER = IMG_FOLDER.resolve("normal/flags");
    public static final Path IMG_DRIVER_PIC_FOLDER = IMG_FOLDER.resolve("normal/drivers");
    public static final Path IMG_TRACK_MAP_FOLDER = IMG_FOLDER.resolve("normal/trackMap");

}
