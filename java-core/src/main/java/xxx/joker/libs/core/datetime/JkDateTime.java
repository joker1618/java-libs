package xxx.joker.libs.core.datetime;

import xxx.joker.libs.core.types.JkFormattable;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class JkDateTime implements Comparable<JkDateTime>, JkFormattable<JkDateTime> {

    private static final DateTimeFormatter DEF_FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private LocalDateTime ldt;

    private JkDateTime(LocalDateTime ldt) {
        init(ldt);
    }

    public static JkDateTime of(long millis) {
        return of(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime());


    }
    public static JkDateTime of(LocalDateTime ldt) {
        return new JkDateTime(ldt);
    }
    public static JkDateTime of(LocalDate ld) {
        return new JkDateTime(LocalDateTime.of(ld, LocalTime.of(0, 0, 0)));
    }

    public static JkDateTime now() {
        return new JkDateTime(LocalDateTime.now());
    }

    public long getTotalMillis() {
        return ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public LocalDateTime getDateTime() {
        return ldt;
    }

    public void setDateTime(LocalDateTime ldt) {
        this.ldt = ldt;
    }

    @Override
    public int compareTo(JkDateTime o) {
        return ldt.compareTo(o.ldt);
    }
    public int compareTo(LocalDateTime oldt) {
        return ldt.compareTo(oldt);
    }

    @Override
    public String format() {
        return ldt.format(DEF_FMT);
    }

    @Override
    public JkDateTime parse(String str) {
        init(LocalDateTime.parse(str, DEF_FMT));
        return this;
    }

    @Override
    public String toString() {
        return format();
    }

    private void init(LocalDateTime ldt) {
        this.ldt = ldt;
    }
}
