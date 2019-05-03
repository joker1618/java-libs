package stuff.crawlers;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import stuff.checkers.CheckRepo;
import xxx.joker.apps.formula1.model.F1Model;
import xxx.joker.apps.formula1.model.F1ModelImpl;
import xxx.joker.apps.formula1.webParser.WikiParser;
import xxx.joker.libs.core.datetime.JkTimer;
import xxx.joker.libs.core.debug.JkDebug;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class WebDataCrawler {

    @BeforeClass
    public static void beforeClass() {
//        JkFiles.deleteContent(F1Const.DB_FOLDER);
    }

    F1Model model;
    @Before
    public void before() {
        JkDebug.startTimer("Init model");
        model = F1ModelImpl.getInstance();
        JkDebug.stopTimer("Init model");
    }

    @Test
    public void runYear() {
        int year = 2018;

        runYear(year);

        F1ModelImpl.getInstance().commit();

    }

    @Test
    public void runRange() {
        int ystart = 1999;
        int yend = 2018;

        for(int y = yend; y >= ystart; y--) {
            runYear(y);
        }

        F1ModelImpl.getInstance().commit();

        new CheckRepo().doAllYearChecks();

    }

    @After
    public void after() {
        JkDebug.displayTimes();
    }

    private void runYear(int year) {
        JkDebug.startTimer("Parse year");
        display("####  Start parsing year {}", year);
        WikiParser parser = WikiParser.getParser(year);
        parser.parse();
        JkDebug.stopTimer("Parse year");
    }

}
