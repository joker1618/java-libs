package xxx.joker.libs.javalibs.datetime;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static xxx.joker.libs.javalibs.utils.JkStrings.strf;

/**
 * Created by f.barbano on 25/05/2018.
 */
public class JkTime {

	private long totalMillis;

//	private long days;
	private long hours;
	private long minutes;
	private long seconds;
	private long milli;

	private JkTime(long totalMillis) {
		this.totalMillis = totalMillis;
		this.milli = totalMillis % 1000;

		long rem = (totalMillis - this.milli) / 1000;

//		long daySec = DAYS.getDuration().getSeconds();
//		this.days = rem / daySec;
//		rem -= daySec * this.days;

		long hourSec = HOURS.getDuration().getSeconds();
		this.hours = rem / hourSec;
		rem -= hourSec * this.hours;

		long minuteSec = MINUTES.getDuration().getSeconds();
		this.minutes = rem / minuteSec;
		rem -= minuteSec * this.minutes;

		this.seconds =  rem;
	}

	public static JkTime of(long totalMillis) {
		return new JkTime(totalMillis);
	}
	public static JkTime of(LocalDateTime ldt) {
		long totmilli = ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		return new JkTime(totmilli);
	}

	public String toStringElapsed(boolean showMilli) {
		StringBuilder sb = new StringBuilder();

		if(hours > 0)	sb.append(strf("%02d:", hours));
		if(minutes > 0)	sb.append(strf("%02d:", minutes));
		sb.append(strf("%02d", seconds));
		if(showMilli)	sb.append(strf(".%03d:", milli));

		return sb.toString();
	}

}
