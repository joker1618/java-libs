package trymodel.entities2;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.utils.JkStrings;
import xxx.joker.libs.repository.design.JkEntityFieldCustom;

import java.util.Comparator;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class CustomEntity implements JkEntityFieldCustom<CustomEntity> {

    private Pair<String, String> pair;

    @Override
    public String formatField() {
        return strf("{}:->{}", pair.getKey(), pair.getValue());
    }

    @Override
    public void setFromString(String str) {
        String[] arr = JkStrings.splitArr(str, ":->");
        pair = Pair.of(arr[0], arr[1]);
    }

    @Override
    public int compareTo(CustomEntity o) {
        int res = StringUtils.compareIgnoreCase(pair.getKey(), o.pair.getKey());
        if(res == 0) {
            res = StringUtils.compareIgnoreCase(pair.getValue(), o.pair.getValue());
        }
        return res;
    }
}
