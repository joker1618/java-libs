package xxx.joker.apps.agenda.console.args;

import xxx.joker.libs.argsparser.design.annotations.JkCmdElem;
import xxx.joker.libs.argsparser.design.classTypes.JkCommands;
import xxx.joker.libs.argsparser.design.descriptors.COption;
import xxx.joker.libs.argsparser.design.descriptors.CParam;
import xxx.joker.libs.argsparser.design.functions.StringCheck;
import xxx.joker.libs.argsparser.design.functions.ValueCheck;
import xxx.joker.libs.core.utils.JkConvert;

import static xxx.joker.apps.agenda.console.args.AgendaArgType.*;
import static xxx.joker.libs.core.utils.JkStrings.strf;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.UnaryOperator;

public enum AgendaCmd implements JkCommands {

    @JkCmdElem
    CMD_ADD_EVENT_CONSOLE(
            new CParam(COption.of(ADD)),
            new CParam(COption.of(EVENT)),
            new CParam(COption.ofDate(DATE, "yyyyMMdd")),
            new CParam(COption.of(TITLE).addChecksBefore(StringCheck.isNotBlank())),
            new CParam(false, COption.ofTime(TIME, "HHmm")),
            new CParam(false, COption.of(TAGS).addChecksBefore(StringCheck.isNotBlank())),
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

    @JkCmdElem
    CMD_DELETE_EVENTS(
            new CParam(COption.of(DELETE)),
            new CParam(COption.of(EVENT)),
            new CParam(COption.of(ID))
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
            int idx = obj.indexOf('?');
            String strPath = idx == -1 ? obj : obj.substring(0, idx);
            Path path = Paths.get(strPath);
            if(!Files.exists(path)) {
                return strf("Attach file '{}' does not exists", path);
            }
            if(idx != -1 && idx == obj.length()) {
                return strf("Empty description for attach: {}", path);
            }
            return null;
        };
    }
}
