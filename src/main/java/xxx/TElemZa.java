package xxx;

import xxx.joker.libs.javalibs.dao.csv.CsvElement;

import static xxx.joker.libs.javalibs.utils.JkConsole.display;

public class TElemZa implements CsvElement {
    @Override
    public String getElemID() {
        return null;
    }

    public TElemZa() {
        display("new instance of "+getClass().getName());
    }
}
