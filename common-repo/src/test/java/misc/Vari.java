package misc;

import org.junit.Test;
import xxx.joker.libs.core.datetime.JkDuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class Vari {

    @Test
    public void erf() throws IOException, ParseException {
        String str = "federico fe no barbano";
        display(str);
        display(str.replaceAll("(^fe|no$)", "XX"));
        display(str.replaceAll("^fe|no$", "XX"));
        display(str.replaceAll(".*rico|nop$", "XX"));
    }

    @Test
    public void provaas() throws IOException, ParseException {
        LocalDateTime ldt = LocalDateTime.now();
        display(ldt.format(DateTimeFormatter.ISO_DATE_TIME));
        display(ldt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }


}