package xxx.joker.libs.repository;

import xxx.joker.libs.core.lambdas.JkStreams;

import java.nio.file.Path;
import java.util.List;

public class RepoResourcerImpl implements RepoResourcer {

    public void saveResource(Path sourcePath, List<String> tags) {
        String subFolder = JkStreams.join(tags, "-");


    }
}
