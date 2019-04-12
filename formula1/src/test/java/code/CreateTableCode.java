package code;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import xxx.joker.libs.core.lambdas.JkStreams;

import java.util.Arrays;
import java.util.List;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class CreateTableCode {

    @Test
    public void cr() {
        List<String> list = Arrays.asList(
                "teamName",
                "teamNation",
                "engine",
                "carNo",
                "driverName",
                "driverNation",
                "driverCity",
                "driverBirthDate"
        );
        list.forEach(s -> {
            display("TableColumn<F1SeasonResult, String> col{} = X_FxUtil.createTableColumnString(\"{}\", \"{}\");",
                    StringUtils.capitalize(s), s.toUpperCase(), s
            );
        });
        display("table.getColumns().addAll({});", JkStreams.join(list, ", ", s -> "col"+StringUtils.capitalize(s)));
    }
}
