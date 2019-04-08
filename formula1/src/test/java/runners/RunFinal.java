package runners;

import org.junit.Test;
import util.ToStringRepo;
import xxx.joker.apps.formula1.parsers.IWikiParser;
import xxx.joker.apps.formula1.model.F1Model;
import xxx.joker.apps.formula1.model.F1ModelImpl;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class RunFinal {

    F1Model model = F1ModelImpl.getInstance();

    @Test
    public void runYear() {
        int year = 2018;

//        model.clearDataSets();
//        model.deleteData(year);

        IWikiParser parser = IWikiParser.getParser(year);
        parser.parse();
        model.commit();

        ToStringRepo printer = new ToStringRepo();
//        printer.showEntrants(year);
//        printer.showGPDescription(year);
//        printer.showGPTimes(year);
//        printer.showLinks();

    }

    @Test
    public void runRange() {
        int ystart = 2017;
        int yend = 2018;
//        model.deleteData(year);

        for(int year = yend; year >= ystart; year--) {
            display("Start parsing year {}", year);
            model.deleteData(year);
            IWikiParser parser = IWikiParser.getParser(year);
            parser.parse();

//
//            printer.showEntrants(year);
        }

        model.commit();

    }
}
