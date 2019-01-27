package xxx.joker.apps.agenda.console.args;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.argsparser.design.classTypes.JkAbstractArgs;

public class AgendaHelp {

    public static final String USAGE = "" +
            "USAGE:" +
            "\n\tagenda  -a|add  -e|event  -dt|date <AOD(8)>  [-tm|time <AOT(4)>]  [-t|tags TAGS]  -tit|title TITLE  [-n|notes NOTES]  [-att|attach PATH[?DESCR]...]" +
            "\n\tagenda  -a|add  -e|event  [-f|files PATHS...]" +
            "\n\tagenda  -s|show  -e|event  [-id|id ID]";
}
