package runner;

import org.junit.Test;
import xxx.joker.libs.repository.util.RepoUtil;
import xxx.joker.service.commonRepo.JkCommonRepo;
import xxx.joker.service.commonRepo.JkCommonRepoImpl;
import xxx.joker.service.commonRepo.entities.JkNation;

import java.nio.file.Path;
import java.nio.file.Paths;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class PrintNations {

    @Test
    public void as() {
        Path p0 = Paths.get("C:\\Users\\fede");
        Path p1 = Paths.get("").toAbsolutePath();
        Path p2 = p0.resolve("Desktop");

        display("{} -- {}", p0.relativize(p1), p0.relativize(p2));
        display("{} {} {}", p1.startsWith(p0), p1.startsWith(p2), p2.startsWith(p0));
        display("{}", p2.relativize(p1));
    }
    @Test
    public void printNations() {
        JkCommonRepo model = JkCommonRepoImpl.getInstance();
        String str = RepoUtil.formatEntities(model.getDataSet(JkNation.class), "code flag");
        display(str);
    }

}
