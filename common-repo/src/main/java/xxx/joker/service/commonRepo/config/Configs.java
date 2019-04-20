package xxx.joker.service.commonRepo.config;

import xxx.joker.libs.core.runtimes.JkEnvironment;

import java.nio.file.Path;

public class Configs {

    public static final Path BASE_FOLDER = JkEnvironment.getAppsFolder().resolve("shared-repo");

    public static final Path TMP_FOLDER = BASE_FOLDER.resolve("tmp");

    public static final Path HTML_FOLDER = BASE_FOLDER.resolve("html");

    public static final Path DB_FOLDER = BASE_FOLDER.resolve("repository");
    public static final String DB_NAME = "sharedDB";


}
