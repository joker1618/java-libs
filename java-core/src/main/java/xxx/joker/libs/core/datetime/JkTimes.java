package xxx.joker.libs.core.datetime;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class JkTimes {

    public static boolean isLocalDate(String source, String format) {
        return isLocalDate(source, DateTimeFormatter.ofPattern(format));
    }
    public static boolean isLocalDate(String source, DateTimeFormatter format) {
        try {
            format.parse(source);
            return true;
        } catch(DateTimeParseException ex) {
            return false;
        }
    }
    public static boolean isLocalTime(String source, String format) {
        return isLocalTime(source, DateTimeFormatter.ofPattern(format));
    }
    public static boolean isLocalTime(String source, DateTimeFormatter format) {
        try {
            format.parse(source);
            return true;
        } catch(Exception ex) {
            return false;
        }
    }
    public static boolean isLocalDateTime(String source, String format) {
        return isLocalDateTime(source, DateTimeFormatter.ofPattern(format));
    }
    public static boolean isLocalDateTime(String source, DateTimeFormatter format) {
        try {
            format.parse(source);
            return true;
        } catch(Exception ex) {
            return false;
        }
    }
    public static boolean areLocalDates(String[] source, DateTimeFormatter format) {
        for(String elem : source) {
            if(!isLocalDate(elem, format)) {
                return false;
            }
        }
        return true;
    }
    public static boolean areLocalTimes(String[] source, DateTimeFormatter format) {
        for(String elem : source) {
            if(!isLocalTime(elem, format)) {
                return false;
            }
        }
        return true;
    }
    public static boolean areLocalDateTimes(String[] source, DateTimeFormatter format) {
        for(String elem : source) {
            if(!isLocalDateTime(elem, format)) {
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
