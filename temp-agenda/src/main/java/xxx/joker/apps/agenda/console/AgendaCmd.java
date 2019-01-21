package xxx.joker.apps.agenda.console;

import xxx.joker.libs.argsparser.design.annotations.JkCmdElem;
import xxx.joker.libs.argsparser.design.classTypes.JkCommands;
import xxx.joker.libs.argsparser.design.descriptors.COption;
import xxx.joker.libs.argsparser.design.descriptors.CParam;
import xxx.joker.libs.argsparser.design.functions.ValueCheck;
import xxx.joker.libs.core.exception.JkRuntimeException;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.utils.JkStrings;

import static xxx.joker.apps.agenda.console.AgendaArgType.*;
import static xxx.joker.libs.core.utils.JkStrings.strf;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public enum AgendaCmd implements JkCommands {

    @JkCmdElem
    CMD_ADD_EVENT_CONSOLE(
            new CParam(COption.of(ADD)),
            new CParam(COption.of(EVENT)),
            new CParam(COption.ofDate(DATE, "yyyyMMdd")),
            new CParam(COption.of(TITLE)),
            new CParam(false, COption.ofTime(TIME, "HHmm")),
            new CParam(false, COption.of(TAGS)),
            new CParam(false, COption.of(NOTES)),
            new CParam(false, COption.of(ATTACHES).addChecksBefore(checkAttachments()))
    ),


    @JkCmdElem
    CMD_ADD_EVENT_INTERACTIVE(
            new CParam(COption.of(ADD)),
            new CParam(COption.of(EVENT)),
            new CParam(false, COption.ofPathsWindows(FILES).addChecksAfter(ValueCheck.isFile()))
    ),

    @JkCmdElem
    CMD_SHOW_EVENTS(
            new CParam(COption.of(SHOW)),
            new CParam(COption.of(EVENT)),
            new CParam(false, COption.of(ID))
    ),


    ;

    private List<CParam> params;

    AgendaCmd(CParam... params) {
        this.params = JkConvert.toArrayList(params);
    }

    @Override
    public List<CParam> params() {
        return params;
    }

    private static UnaryOperator<String> checkAttachments() {
        return obj -> {
            Path path = Paths.get(JkStrings.splitArr(obj, "?")[0]);
            if(!Files.exists(path)) {
                return strf("Attach file '{}' does not exists", path);
            }
            return null;
        };
    }
}
