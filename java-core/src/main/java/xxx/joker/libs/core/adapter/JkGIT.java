package xxx.joker.libs.core.adapter;

import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.files.JkFiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JkGIT {

    private Path gitFolder;
    private String gitUrl;

    public JkGIT(Path gitFolder, String gitUrl) {
        this.gitFolder = gitFolder;
        this.gitUrl = gitUrl;
    }

    public JkProcess clone() {
        try {
            Path parent = JkFiles.getParent(gitFolder);
            Files.createDirectories(parent);
            JkFiles.delete(gitFolder);
            return JkProcess.execute(parent, "git clone {} {}", gitUrl, gitFolder.getFileName());

        } catch (IOException e) {
            throw new JkRuntimeException(e);
        }
    }

    public JkProcess pull() {
        return JkProcess.execute(gitFolder, "git pull");
    }

    public void commitAndPush(String commitMex) {
        JkProcess.execute(gitFolder, "git add --all");
        JkProcess.execute(gitFolder, "git commit -m {}", commitMex);
        JkProcess.execute(gitFolder, "git push");
    }

}
