package xxx.joker.libs.core.datetime;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static xxx.joker.libs.core.utils.JkStrings.strf;

public class JkTimes {

    public static final DateTimeFormatter DTF_AOD = DateTimeFormatter.ofPattern("yyyyMMdd");

    /* OUTPUTS */
    public static String toStringElapsed(long elapsed) {
        return toStringElapsed(elapsed, false, SECONDS);
    }
    public static String toStringElapsed(long elapsed, ChronoUnit minUnit) {
        return toStringElapsed(elapsed, false, minUnit);
    }
    public static String toStringElapsed(long elapsed, boolean showMilli) {
        return toStringElapsed(elapsed, showMilli, SECONDS);
    }
    public static String toStringElapsed(long elapsed, boolean showMilli, ChronoUnit minUnit) {
        return JkDuration.of(elapsed).toStringElapsed(showMilli, minUnit);
    }


    /* CHECKS */
    public static boolean isAOD(String source) {
        try {
            DTF_AOD.parse(source);
            return true;
        } catch(DateTimeParseException ex) {
            return false;
        }
    }
    public static boolean areAODs(List<String> source) {
        for(String elem : source) {
            if(!isAOD(elem)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isLocalDate(String source, String format) {
        return isLocalDate(source, DateTimeFormatter.ofPattern(format));
    }
    public static boolean isLocalDate(String source, DateTimeFormatter formatter) {
        try {
            formatter.parse(source);
            return true;
        } catch(DateTimeParseException ex) {
            return false;
        }
    }
    public static boolean areLocalDates(List<String> source, String format) {
        return areLocalDates(source, DateTimeFormatter.ofPattern(format));
    }
    public static boolean areLocalDates(List<String> source, DateTimeFormatter formatter) {
        for(String elem : source) {
            if(!isLocalDate(elem, formatter)) {
                return false;
            }
        }
        return true;
    }
    public static boolean areLocalDates(String[] sarr, DateTimeFormatter formatter) {
        return areLocalDates(Arrays.asList(sarr), formatter);
    }

    public static boolean isLocalTime(String source, String format) {
        return isLocalTime(source, DateTimeFormatter.ofPattern(format));
    }
    public static boolean isLocalTime(String source, DateTimeFormatter formatter) {
        try {
            formatter.parse(source);
            return true;
        } catch(Exception ex) {
            return false;
        }
    }
    public static boolean areLocalTimes(List<String> source, String format) {
        return areLocalTimes(source, DateTimeFormatter.ofPattern(format));
    }
    public static boolean areLocalTimes(List<String> source, DateTimeFormatter formatter) {
        for(String elem : source) {
            if(!isLocalTime(elem, formatter)) {
                return false;
            }
        }
        return true;
    }
    public static boolean areLocalTimes(String[] sarr, DateTimeFormatter formatter) {
        return areLocalTimes(Arrays.asList(sarr), formatter);
    }

    public static boolean isLocalDateTime(String source, String format) {
        return isLocalDateTime(source, DateTimeFormatter.ofPattern(format));
    }
    public static boolean isLocalDateTime(String source, DateTimeFormatter formatter) {
        try {
            formatter.parse(source);
            return true;
        } catch(Exception ex) {
            return false;
        }
    }
    public static boolean areLocalDateTimes(List<String> source, String format) {
        return areLocalDateTimes(source, DateTimeFormatter.ofPattern(format));
    }
    public static boolean areLocalDateTimes(List<String> source, DateTimeFormatter formatter) {
        for(String elem : source) {
            if(!isLocalDateTime(elem, formatter)) {
                return false;
            }
        }
        return true;
    }
    public static boolean areLocalDateTimes(String[] sarr, DateTimeFormatter formatter) {
        return areLocalDateTimes(Arrays.asList(sarr), formatter);
    }

    public static boolean isValidDateTimeFormatter(String format) {
        try {
            DateTimeFormatter.ofPattern(format);
            return true;
        } catch(Exception ex) {
            return false;
        }
    }


}
