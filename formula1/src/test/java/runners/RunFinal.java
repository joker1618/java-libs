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
        int year = 2017;
        model.deleteData(year);

        IWikiParser parser = IWikiParser.getParser(year);
        parser.parse();
//        model.commit();

        ToStringRepo printer = new ToStringRepo();
//        printer.showEntrants(year);
//        printer.showGPDescription(year);
//        printer.showGPTimes(year);

    }
}
