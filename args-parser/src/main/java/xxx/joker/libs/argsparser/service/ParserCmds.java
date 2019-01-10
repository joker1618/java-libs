package xxx.joker.libs.argsparser.service;

import xxx.joker.libs.argsparser.design.classTypes.JkArgsNames;
import xxx.joker.libs.argsparser.design.classTypes.JkCommands;

class ParserCmds {

    private Class<? extends JkCommands> cmdsClass;

    public ParserCmds(Class<? extends JkCommands> cmdsClass, ParserArgs parserArgs, boolean checkDesign) {
        this.cmdsClass = cmdsClass;
    }
}
