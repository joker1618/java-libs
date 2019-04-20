package runner;

import org.junit.Test;
import xxx.joker.libs.repository.entities.RepoResource;
import xxx.joker.libs.repository.entities.RepoUri;
import xxx.joker.libs.repository.util.RepoUtil;
import xxx.joker.service.commonRepo.JkCommonRepo;
import xxx.joker.service.commonRepo.JkCommonRepoImpl;
import xxx.joker.service.commonRepo.entities.JkNation;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class PrintNations {

    JkCommonRepo model = JkCommonRepoImpl.getInstance();

    @Test
    public void printNations() {
        String str = RepoUtil.formatEntities(model.getDataSet(JkNation.class), "name code");
        display(str+"\n");
    }

    @Test
    public void printAll() {

        String str = RepoUtil.formatEntities(model.getDataSet(JkNation.class));
        display(str+"\n");

        str = RepoUtil.formatEntities(model.getDataSet(RepoResource.class));
        display(str+"\n");

        str = RepoUtil.formatEntities(model.getDataSet(RepoUri.class));
        display(str+"\n");
    }


    @Test
    public void dsdf() {
        display(LocalDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Test
    public void as() {
        Path p0 = Paths.get("C:\\Users\\fede");
        Path p1 = Paths.get("").toAbsolutePath();
        Path p2 = p0.resolve("Desktop");

        display("{} -- {}", p0.relativize(p1), p0.relativize(p2));
        display("{} {} {}", p1.startsWith(p0), p1.startsWith(p2), p2.startsWith(p0));
        display("{}", p2.relativize(p1));
    }


}
