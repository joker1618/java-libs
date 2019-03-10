package xxx.joker.libs.core.datetime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public class JkDateTime implements Comparable<JkDateTime>{

    private LocalDateTime ldt;

    private JkDateTime(LocalDateTime ldt) {
        this.ldt = ldt;
    }

    public static JkDateTime of(LocalDateTime ldt) {
        return new JkDateTime(ldt);
    }
    public static JkDateTime of(LocalDate ld) {
        return new JkDateTime(LocalDateTime.of(ld, LocalTime.of(0, 0, 0)));
    }

    public long getTotalMillis() {
        return ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    @Override
    public int compareTo(JkDateTime o) {
        return ldt.compareTo(o.ldt);
    }
}
