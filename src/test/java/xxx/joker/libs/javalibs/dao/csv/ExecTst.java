package xxx.joker.libs.javalibs.dao.csv;

import org.junit.Test;
import xxx.joker.libs.javalibs.repository.JkDefaultRepoTable;
import xxx.joker.libs.javalibs.utils.JkStrings;

import static xxx.joker.libs.javalibs.utils.JkConsole.display;

public class ExecTst {

    @Test
    public void trytest() {
        CA ca1 = new CA();
        ca1.name = "  FEDE  ";
        CA ca2 = new CA();
        ca2.name = "fede";
        CA ca3 = new CA();
        ca3.name = "f ede";

        display("%s", ca1.equals(ca2));
        display("%s", ca1.equals(ca3));
        display("%s", ca2.equals(ca3));

        CB cb1 = new CB("fede");
        display("%s", ca2.equals(cb1));

    }


}

class CA extends JkDefaultRepoTable {

    String name;

    @Override
    public String getPrimaryKey() {
        return JkStrings.safeTrim(name).toLowerCase();
    }
}
class CB extends JkDefaultRepoTable {

    String name;

    public CB(String name) {
        this.name = name;
    }

    @Override
    public String getPrimaryKey() {
        return JkStrings.safeTrim(name).toLowerCase();
    }
}
