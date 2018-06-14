package xxx.joker.libs.javalibs.datetime;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static xxx.joker.libs.javalibs.utils.JkStrings.strf;

/**
 * Created by f.barbano on 25/05/2018.
 */
public class JkTime {

	private long totalMillis;

	private int hours;
	private int minutes;
	private int seconds;
	private int milli;

	private JkTime(long totalMillis) {
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

	public static JkTime now() {
		return of(LocalDateTime.now());
	}
	public static JkTime of(long totalMillis) {
		return new JkTime(totalMillis);
	}
	public static JkTime of(long amount, ChronoUnit chronoUnit) {
		long totMillis = amount * chronoUnit.getDuration().get(MILLIS);
		return of(totMillis);
	}
	public static JkTime of(Duration duration) {
		return of(duration.toMillis());
	}
	public static JkTime of(LocalDateTime ldt) {
		long totmilli = ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		return of(totmilli);
	}
	public static JkTime of(LocalDate ld) {
		long totmilli = LocalDateTime.of(ld, LocalTime.of(0, 0, 0)).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		return of(totmilli);
	}
	public static JkTime of(Date date) {
		LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
		return of(ldt);
	}

	public String toStringElapsed(boolean showMilli) {
		return toStringElapsed(showMilli, null);
	}
	public String toStringElapsed(boolean showMilli, ChronoUnit minUnit) {
		StringBuilder sb = new StringBuilder();

		if(hours > 0 || minUnit == HOURS) {
			sb.append(strf("%02d:", hours));
			sb.append(strf("%02d:", minutes));
			sb.append(strf("%02d", seconds));
		} else if(minutes > 0 || minUnit == MINUTES) {
			sb.append(strf("%02d:", minutes));
			sb.append(strf("%02d", seconds));
		} else {
			sb.append(strf("%02d", seconds));
		}

		if(showMilli) {
			sb.append(strf(".%03d:", milli));
		}

		return sb.toString();
	}
	public String toStringDateTime(String pattern) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
		return dtf.format(getLocalDateTime());
	}

	public LocalDateTime getLocalDateTime() {
		return Instant.ofEpochMilli(totalMillis).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public long getTotalMillis() {
		return totalMillis;
	}
}
