package runners;

import org.junit.Test;
import util.ToStringRepo;
import xxx.joker.apps.formula1.parsers.IWikiParser;
import xxx.joker.apps.formula1.model.F1Model;
import xxx.joker.apps.formula1.model.F1ModelImpl;
import xxx.joker.libs.core.format.JkOutput;

import java.util.List;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class RunFinal {

    F1Model model = F1ModelImpl.getInstance();

    @Test
    public void runYear() {
        int year = 2018;
        IWikiParser parser = IWikiParser.getParser(year);
        parser.parse();
//        model.commit();
//        display(ToStringRepo.toCols(model.getEntrants(year)));
    }
}
