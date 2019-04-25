package stuff.crawlers;

import org.junit.BeforeClass;
import org.junit.Test;
import xxx.joker.apps.formula1.model.F1ModelImpl;
import xxx.joker.apps.formula1.webParser.WikiParser;
import xxx.joker.libs.core.datetime.JkTimer;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class WebDataCrawler {

    @BeforeClass
    public static void beforeClass() {
//        JkFiles.deleteContent(F1Const.DB_FOLDER);
    }

    @Test
    public void runYear() {
        int year = 2011;
        JkTimer timer = new JkTimer();

        runYear(year);

        F1ModelImpl.getInstance().commit();
        display("Total time: {}", timer.toStringElapsed());

    }

    @Test
    public void runRange() {
        int ystart = 2013;
        int yend = 2018;

        JkTimer timer = new JkTimer();

        for(int y = yend; y >= ystart; y--) {
            runYear(y);
        }

        F1ModelImpl.getInstance().commit();

        display("Total time: {}", timer.toStringElapsed());
    }

    private void runYear(int year) {
        display("####  Start parsing year {}", year);
        WikiParser parser = WikiParser.getParser(year);
        parser.parse();
    }

}
