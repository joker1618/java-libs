package spikes;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class Misc {



    @Test
    public void misc(){
        String str = "fedeRiCO";
        Pattern p = Pattern.compile("([A-Z])");
        Matcher m = p.matcher(str);
        String res = "";
        if(m.find()) {
            res = m.replaceAll(" " + m.group(1));
        }
        display("{}  -->  {}", str, res);
    }
}
