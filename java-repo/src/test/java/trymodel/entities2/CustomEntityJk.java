package trymodel.entities2;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import xxx.joker.libs.core.utils.JkStrings;
import xxx.joker.libs.repository.design.JkRepoFieldCustom;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class CustomEntityJk implements JkRepoFieldCustom<CustomEntityJk> {

    private Pair<String, String> pair;

    @Override
    public String formatField() {
        return strf("{}:->{}", pair.getKey(), pair.getValue());
    }

    @Override
    public void parseString(String str) {
        String[] arr = JkStrings.splitArr(str, ":->");
        pair = Pair.of(arr[0], arr[1]);
    }

    @Override
    public int compareTo(CustomEntityJk o) {
        int res = StringUtils.compareIgnoreCase(pair.getKey(), o.pair.getKey());
        if(res == 0) {
            res = StringUtils.compareIgnoreCase(pair.getValue(), o.pair.getValue());
        }
        return res;
    }

    public Pair<String, String> getPair() {
        return pair;
    }

    public void setPair(Pair<String, String> pair) {
        this.pair = pair;
    }

    @Override
    public String toString() {
        return formatField();
    }
}
