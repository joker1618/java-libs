package xxx.joker.apps.agenda.console.args;

import xxx.joker.libs.argsparser.design.annotations.JkArg;
import xxx.joker.libs.argsparser.design.annotations.JkArgType;
import xxx.joker.libs.argsparser.design.classTypes.JkArgsTypes;
import xxx.joker.libs.argsparser.design.classTypes.JkCommands;
import xxx.joker.libs.argsparser.design.descriptors.CParam;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public enum AgendaArgType implements JkArgsTypes {

    @JkArgType
    ADD("add"),
    @JkArgType
    SHOW("show"),
    @JkArgType
    EVENT("event"),
    @JkArgType
    DATE("date"),
    @JkArgType
    TIME("time"),
    @JkArgType
    TITLE("title"),
    @JkArgType
    TAGS("tags"),
    @JkArgType
    NOTES("notes"),
    @JkArgType
    ATTACHES("attach"),
    @JkArgType
    FILES("files"),
    @JkArgType
    ID("id"),

    ;

    private String argName;

    AgendaArgType(String argName) {
        this.argName = argName;
    }

    @Override
    public String getArgName() {
        return argName;
    }
}
