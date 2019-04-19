package xxx.joker.service.commonRepo.config;

import xxx.joker.libs.core.runtimes.JkEnvironment;

import java.nio.file.Path;

public class Configs {

    public static final Path BASE_FOLDER = JkEnvironment.getAppsFolder().resolve("common-repo");

    public static final Path HTML_FOLDER = BASE_FOLDER.resolve("html");

    public static final Path DB_FOLDER = BASE_FOLDER.resolve("repository");
    public static final String DB_NAME = "commonDB";

    public static final Path DATA_FOLDER = BASE_FOLDER.resolve("data");
    public static final Path FLAGS_FOLDER = DATA_FOLDER.resolve("flags");
    public static final Path FLAGS_FOLDER_IMAGE = FLAGS_FOLDER.resolve("images");
    public static final Path FLAGS_FOLDER_ICON = FLAGS_FOLDER.resolve("icons");

}
