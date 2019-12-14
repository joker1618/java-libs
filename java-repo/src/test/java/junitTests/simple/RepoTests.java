package junitTests.simple;

import org.junit.Before;
import org.junit.Test;
import xxx.joker.libs.core.file.JkFiles;
import xxx.joker.libs.core.test.JkDataTest;
import xxx.joker.libs.repo.JkRepo;
import xxx.joker.libs.repo.util.RepoUtil;

import java.nio.file.Path;
import java.nio.file.Paths;

public class RepoTests {

    public static final Path BASE_FOLDER = Paths.get("src/test/resources/repos");
    public static final String DB_NAME = "simple";

    JkDataTest dataTest = new JkDataTest(666);

    JkRepo.Builder repoBuilder;

    @Before
    public void before() {
        repoBuilder = JkRepo.builder()
                .setRepoFolder(BASE_FOLDER.resolve(DB_NAME))
                .setDbName(DB_NAME)
                .addClasses(SimpleObject.class);
    }

    @Test
    public void testSimpleObject() {
        JkFiles.delete(repoBuilder.buildCtx().getDbFolder());
        JkRepo repo = repoBuilder.buildRepo();
        int tot = 10;
        for(int i = 0; i < tot ; i++)
            repo.add(new SimpleObject(dataTest));
        repo.commit();
    }

    @Test
    public void rewriteDbWithoutCreationTm() {
        JkRepo repo = repoBuilder.buildRepo();
        RepoUtil.rewriteDbWithoutCreationTm(repo.getRepoCtx().getDbFolder());
    }

}
