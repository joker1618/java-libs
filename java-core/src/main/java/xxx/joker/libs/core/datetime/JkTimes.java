package xxx.joker.libs.core.datetime;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class JkTimes {

    public static final DateTimeFormatter DTF_AOD = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static boolean isAOD(String source) {
        try {
            DTF_AOD.parse(source);
            return true;
        } catch(DateTimeParseException ex) {
            return false;
        }
    }
    public static boolean areAod(List<String> source) {
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

    public static boolean isValidDateTimeFormatter(String format) {
        try {
            DateTimeFormatter.ofPattern(format);
            return true;
        } catch(Exception ex) {
            return false;
        }
    }


}
