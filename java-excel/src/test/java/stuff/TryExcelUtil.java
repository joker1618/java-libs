package stuff;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class TryExcelUtil {

    @Test
    public void tryRead() {
        LocalDate ld = LocalDate.parse("20181212", DateTimeFormatter.ofPattern("yyyyMMdd"));
        display("%s", ld);
        LocalDateTime ldt = LocalDateTime.of(ld, LocalTime.MIN);
        display("%s", ldt);
        display("%s", DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss.SSS").format(ldt));
    }
}
