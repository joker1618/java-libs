package steps;

import org.junit.Test;
import util.CheckPoints;
import util.ToStringRepo;
import xxx.joker.apps.formula1.common.F1Const;
import xxx.joker.apps.formula1.model.F1Model;
import xxx.joker.apps.formula1.model.F1ModelImpl;
import xxx.joker.apps.formula1.parsers.WikiParser;
import xxx.joker.libs.core.files.JkFiles;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class A_ParseWiki {

    F1Model model = F1ModelImpl.getInstance();

    @Test
    public void runYear() {
        int year = 2018;

        JkFiles.delete(F1Const.DB_FOLDER);
//        model.clearDataSets();
//        model.deleteData(year);

        WikiParser parser = WikiParser.getParser(year);
        parser.parse();
        model.commit();

        ToStringRepo printer = new ToStringRepo();
        printer.showEntrants(year);
        printer.showGPDescription(year);
        printer.showGPTimes(year);

        new CheckPoints().checkPoints(year);

    }

    @Test
    public void runRange() {
        int ystart = 2017;
        int yend = 2018;
//        model.deleteData(year);

        for(int year = yend; year >= ystart; year--) {
            display("Start parsing year {}", year);
            model.deleteData(year);
            WikiParser parser = WikiParser.getParser(year);
            parser.parse();

//
//            printer.showEntrants(year);
        }

        model.commit();

    }
}
