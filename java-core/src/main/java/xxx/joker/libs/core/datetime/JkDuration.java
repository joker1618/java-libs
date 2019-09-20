package xxx.joker.libs.core.datetime;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.core.format.JkFormattable;
import xxx.joker.libs.core.utils.JkStrings;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static java.time.temporal.ChronoUnit.*;
import static xxx.joker.libs.core.utils.JkStrings.strf;

public class JkDuration implements JkFormattable, Comparable<JkDuration> {
    private long totalMillis;

    private int hours;
    private int minutes;
    private int seconds;
    private int milli;

    public JkDuration() {

    }
    private JkDuration(long totalMillis) {
        init(totalMillis);
    }
    private void init(long totalMillis) {
        this.totalMillis = totalMillis;

        long absTotalMillis = Math.abs(totalMillis);
        this.milli = (int) absTotalMillis % 1000;

        long rem = (absTotalMillis - this.milli) / 1000;

        long hourSec = HOURS.getDuration().getSeconds();
        this.hours = (int)(rem / hourSec);
        rem -= hourSec * this.hours;

        long minuteSec = MINUTES.getDuration().getSeconds();
        this.minutes = (int)(rem / minuteSec);
        rem -= minuteSec * this.minutes;

        this.seconds =  (int)rem;
    }

    public static JkDuration of(double totalMillis) {
        return new JkDuration((long)totalMillis);
    }
    public static JkDuration of(long totalMillis) {
        return new JkDuration(totalMillis);
    }
    public static JkDuration of(Duration duration) {
        return of(duration.toMillis());
    }
    public static JkDuration of(javafx.util.Duration duration) {
        return of(duration.toMillis());
    }
    public static JkDuration of(String elapsed) {
        try {
            int sign = elapsed.startsWith("-") ? -1 : 1;
            String strElapsed = elapsed.replaceAll("^[\\+-][ ]*", "");
            String[] splitMs = JkStrings.splitArr(strElapsed, ".");
            String[] splitTm = JkStrings.splitArr(splitMs[0], ":");
            long ms = 0L;
            if (splitMs.length > 1) ms += Long.parseLong(splitMs[1]);
            for (int i = splitTm.length - 1, mult = 1000; i >= 0; i--, mult *= 60) {
                ms += Long.parseLong(splitTm[i]) * mult;
            }
            return of(ms * sign);
        } catch(Exception ex) {
            return null;
        }
    }
    public static JkDuration untilNow(long startMillis) {
        return of(System.currentTimeMillis() - startMillis);
    }

    public String toStringElapsed() {
        return toStringElapsed(true);
    }
    public String toStringElapsed(boolean showMilli) {
        return toStringElapsed(showMilli, SECONDS);
    }
    public String toStringElapsed(ChronoUnit minUnit) {
        return toStringElapsed(true, minUnit);
    }
    public String toStringElapsed(boolean showMilli, ChronoUnit minUnit) {
        StringBuilder sb = new StringBuilder();

        if(getHours() > 0 || minUnit == HOURS) {
            sb.append(strf("%02d:", getHours()));
            sb.append(strf("%02d:", getMinutes()));
            sb.append(strf("%02d", getSeconds()));
        } else if(getMinutes() > 0 || minUnit == MINUTES) {
            sb.append(strf("%02d:", getMinutes()));
            sb.append(strf("%02d", getSeconds()));
        } else {
            sb.append(strf("%d", getSeconds()));
        }

        if(showMilli) {
            String smilli = String.valueOf(getMilli());
            smilli += StringUtils.repeat('0', 3 - smilli.length());
            sb.append(strf(".{}", smilli));
        }

        if(totalMillis < 0L) {
            sb.insert(0, "- ");
        }

        return sb.toString();
    }

    public static String toStringElapsed(long milli) {
        return JkDuration.of(milli).toStringElapsed();
    }
    public static String toStringElapsed(long milli, boolean showMilli) {
        return JkDuration.of(milli).toStringElapsed(showMilli);
    }
    public static String toStringElapsed(long milli, ChronoUnit minUnit) {
        return JkDuration.of(milli).toStringElapsed(minUnit);
    }
    public static String toStringElapsed(long milli, boolean showMilli, ChronoUnit minUnit) {
        return JkDuration.of(milli).toStringElapsed(showMilli, minUnit);
    }

    public long toMillis() {
        return totalMillis;
    }
	public int getHours() {
        return hours;
    }
	public int getMinutes() {
        return minutes;
    }
	public int getSeconds() {
        return seconds;
    }
	public int getMilli() {
        return milli;
    }

    public JkDuration plus(JkDuration toAdd) {
        return JkDuration.of(totalMillis + toAdd.totalMillis);
    }

    public JkDuration minus(JkDuration toSubtract) {
        return JkDuration.of(totalMillis - toSubtract.totalMillis);
    }

    public JkDuration diff(JkDuration toSubtract) {
        return minus(toSubtract);
    }

    public javafx.util.Duration toFxDuration() {
        return javafx.util.Duration.millis(totalMillis);
    }

    @Override
    public String format() {
        return String.valueOf(totalMillis);
    }

    @Override
    public JkDuration parse(String str) {
        init(Long.valueOf(str));
        return this;
    }

    @Override
    public int compareTo(JkDuration o) {
        return (int) (totalMillis - o.toMillis());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JkDuration that = (JkDuration) o;
        return totalMillis == that.totalMillis;
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalMillis);
    }
}
