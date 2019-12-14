package xxx.joker.libs.core.adapter;

import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.file.JkFiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JkGit {

    private Path localFolder;
    private String remoteUrl;

    public JkGit(Path localFolder, String remoteUrl) {
        this.localFolder = localFolder;
        this.remoteUrl = remoteUrl;
    }

    public JkProcess clone() {
        try {
            Path parent = JkFiles.getParent(localFolder);
            Files.createDirectories(parent);
            JkFiles.delete(localFolder);
            return JkProcess.execute(parent, "git clone {} {}", remoteUrl, localFolder.getFileName());

        } catch (IOException e) {
            throw new JkRuntimeException(e);
        }
    }

    public JkProcess pull() {
        return JkProcess.execute(localFolder, "git pull");
    }

    public List<JkProcess> commitAndPush(String commitMex) {
        List<JkProcess> resList = new ArrayList<>();
        resList.add(JkProcess.execute(localFolder, "git add --all"));
        resList.add(JkProcess.execute(localFolder, "git commit -m {}", commitMex));
        resList.add(JkProcess.execute(localFolder, "git push"));
        return resList;
    }

    public List<JkProcess> setCommitter(String userName, String userMail) {
        List<JkProcess> resList = new ArrayList<>();
        resList.add(JkProcess.execute(localFolder, "git config user.name \"{}\"", userName));
        resList.add(JkProcess.execute(localFolder, "git config user.email \"{}\"", userMail));
        return resList;
    }

    public Path getLocalFolder() {
        return localFolder;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }
}
