package stuff;

import org.junit.Test;
import xxx.joker.libs.core.datetime.JkDateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static xxx.joker.libs.core.util.JkConsole.display;

public class TryExcelUtil {

    @Test
    public void tryRead() {
        LocalDate ld = LocalDate.parse("20181212", DateTimeFormatter.ofPattern("yyyyMMdd"));
        display("%s", ld);
        LocalDateTime ldt = LocalDateTime.of(ld, LocalTime.MIN);
        display("%s", ldt);
        display("%s", DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS").format(ldt));
    }

    @Test
    public void aa() {
        JkDateTime now = JkDateTime.now();
        display(now.format());
    }
}
