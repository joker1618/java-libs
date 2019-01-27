package xxx.joker.apps.agenda.common;

import xxx.joker.libs.core.runtimes.JkEnvironment;

import java.nio.file.Path;

public class AgendaConst {

    public static Path BASE_FOLDER = JkEnvironment.getAppTempFolder().resolve("agenda");
    public static Path DB_FOLDER = BASE_FOLDER.resolve("db");
    public static Path ATTACHES_FOLDER = BASE_FOLDER.resolve("attachments");
}
