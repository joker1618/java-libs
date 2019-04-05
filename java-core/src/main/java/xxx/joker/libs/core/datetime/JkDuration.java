package xxx.joker.libs.core.datetime;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.utils.JkStrings;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static java.time.temporal.ChronoUnit.*;
import static xxx.joker.libs.core.utils.JkStrings.strf;

public class JkDuration {
    private long totalMillis;

    private int hours;
    private int minutes;
    private int seconds;
    private int milli;

    private JkDuration(long totalMillis) {
        this.totalMillis = totalMillis;
        this.milli = (int) totalMillis % 1000;

        long rem = (totalMillis - this.milli) / 1000;

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
        if(StringUtils.isBlank(elapsed))    return null;
        String[] splitMs = JkStrings.splitArr(elapsed, ".");
        String[] splitTm = JkStrings.splitArr(splitMs[0], ":");
        long ms = 0L;
        if(splitMs.length == 2)     ms += JkConvert.toLong(splitMs[1]);
        for(int i = splitTm.length - 1, mult = 1000; i >= 0; i--, mult *= 60) {
            ms += JkConvert.toLong(splitTm[i]) * mult;
        }
        return of(ms);
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
            sb.append(strf("%d:", getHours()));
            sb.append(strf("%02d:", getMinutes()));
            sb.append(strf("%02d", getSeconds()));
        } else if(getMinutes() > 0 || minUnit == MINUTES) {
            sb.append(strf("%d:", getMinutes()));
            sb.append(strf("%02d", getSeconds()));
        } else {
            sb.append(strf("%d", getSeconds()));
        }

        if(showMilli) {
            sb.append(strf(".%03d", getMilli()));
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

    public JkDuration add(JkDuration toAdd) {
        return JkDuration.of(totalMillis + toAdd.totalMillis);
    }
}
