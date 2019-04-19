package xxx.joker.apps.formula1.dataCreator.model.fields;

import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.core.utils.JkStrings;
import xxx.joker.libs.core.types.JkFormattable;

import static xxx.joker.libs.core.utils.JkStrings.strf;

public class F1FastLap implements JkFormattable {

    private static final String SEP = "::";

    private String driverPK;
    private JkDuration lapTime;

    public F1FastLap() {

    }
    public F1FastLap(String driverPK, JkDuration lapTime) {
        this.driverPK = driverPK;
        this.lapTime = lapTime;
    }

    @Override
    public String toString() {
        return format();
    }

    @Override
    public String format() {
        return strf("{}{}{}", driverPK, SEP, lapTime.toMillis());
    }

    @Override
    public F1FastLap parse(String str) {
        String[] split = JkStrings.splitArr(str, SEP, true);
        driverPK = split[0];
        lapTime = JkDuration.of(Long.valueOf(split[1]));
        return this;
    }

    public String toLine() {
        return strf("{}, {}", driverPK, lapTime.toStringElapsed());
    }

    public String getDriverPK() {
        return driverPK;
    }

    public void setDriverPK(String driverPK) {
        this.driverPK = driverPK;
    }

    public JkDuration getLapTime() {
        return lapTime;
    }

    public void setLapTime(JkDuration lapTime) {
        this.lapTime = lapTime;
    }
}
