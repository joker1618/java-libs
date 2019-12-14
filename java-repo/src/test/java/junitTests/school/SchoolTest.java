package junitTests.school;

import org.junit.Before;
import org.junit.Test;
import xxx.joker.libs.core.file.JkEncryption;
import xxx.joker.libs.core.file.JkFiles;
import xxx.joker.libs.core.format.JkViewBuilder;
import xxx.joker.libs.core.test.JkDataTest;
import xxx.joker.libs.repo.JkRepo;
import xxx.joker.libs.repo.config.RepoCtx;
import xxx.joker.libs.repo.util.RepoUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static xxx.joker.libs.core.lambda.JkStreams.toMap;
import static xxx.joker.libs.core.lambda.JkStreams.toMapSingle;
import static xxx.joker.libs.core.util.JkConsole.display;
import static xxx.joker.libs.core.util.JkConsole.displayColl;
import static xxx.joker.libs.core.util.JkStrings.strf;

public class SchoolTest {

    public static final Path BASE_FOLDER = Paths.get("src/test/resources/repos");
    public static final String DB_NAME = "school";

    static final JkDataTest dataTest = new JkDataTest(3433);
    JkRepo.Builder repoBuilder;

    @Before
    public void before() {
        repoBuilder = JkRepo.builder()
                .setRepoFolder(BASE_FOLDER.resolve(DB_NAME))
                .setDbName(DB_NAME)
                .addClasses(ClassRoom.class)
                .addPackage("junitTests.school");
    }

    @Test
    public void fullTest() {
        RepoCtx ctx = repoBuilder.buildCtx();

        // 1. delete repo
        JkFiles.delete(ctx.getRepoFolder());
        // 2. create & commit
        testSchool1();
        // 3. remove creation tm from files
        Path folder1 = RepoUtil.rewriteDbWithoutCreationTm(ctx.getDbFolder());
        // 4. read & commit
        repoBuilder.buildRepo().commit();
        // 5. remove creation tm from files
        Path folder2 = RepoUtil.rewriteDbWithoutCreationTm(ctx.getDbFolder());
        // 6. compare files
        Map<String, String> map1 = toMapSingle(JkFiles.findFiles(folder1, false), p -> p.getFileName().toString(), JkEncryption::getMD5);
        Map<String, String> map2 = toMapSingle(JkFiles.findFiles(folder2, false), p -> p.getFileName().toString(), JkEncryption::getMD5);
        JkViewBuilder vb = new JkViewBuilder();
        vb.addLines("file_name|md5 {}|md5 {}|res", folder1.getFileName(), folder2.getFileName());
        map1.forEach((fn1, h1) -> vb.addLines(strf("{}|{}|{}|{}", fn1, h1, map2.get(fn1), h1.equals(map2.get(fn1)) ? "eq" : "diff")));
        display("Compare folders: {}, {}", folder1, folder2);
        displayColl(vb.toLines("|", 2));
    }

    @Test
    public void testSchool1() {
        JkRepo repo = repoBuilder.buildRepo();
        List<LessonsPlan> plans = dataTest.nextElements(() -> new LessonsPlan(dataTest), 2);
        repo.addAll(plans);
        repo.commit();
    }

    @Test
    public void rewriteDbWithoutCreationTm() {
        JkRepo repo = repoBuilder.buildRepo();
        RepoUtil.rewriteDbWithoutCreationTm(repo.getRepoCtx().getDbFolder());
    }

}
