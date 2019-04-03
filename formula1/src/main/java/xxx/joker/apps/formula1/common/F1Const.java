package xxx.joker.apps.formula1.common;

import xxx.joker.libs.core.runtimes.JkEnvironment;

import java.nio.file.Path;

public class F1Const {

    public static final Path BASE_FOLDER = JkEnvironment.getAppsFolder().resolve("formula1");

    public static final Path HTML_FOLDER = BASE_FOLDER.resolve("html");

    public static final Path DB_FOLDER = BASE_FOLDER.resolve("repository");
    public static final String DB_NAME = "f1";
}
