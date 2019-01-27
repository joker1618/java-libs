package xxx.joker.libs.core.datetime;

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

    public String toStringElapsed() {
        return toStringElapsed(false, SECONDS);
    }
    public String toStringElapsed(boolean showMilli) {
        return toStringElapsed(showMilli, SECONDS);
    }
    public String toStringElapsed(ChronoUnit minUnit) {
        return toStringElapsed(false, minUnit);
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
            sb.append(strf("%02d", getSeconds()));
        }

        if(showMilli) {
            sb.append(strf(".%03d", getMilli()));
        }

        return sb.toString();
    }

    public long getTotalMillis() {
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
}
