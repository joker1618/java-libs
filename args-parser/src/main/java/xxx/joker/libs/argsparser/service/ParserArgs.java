package xxx.joker.libs.argsparser.service;

import xxx.joker.libs.argsparser.design.classTypes.JkAbstractArgs;
import xxx.joker.libs.argsparser.design.classTypes.JkArgsNames;

class ParserArgs {

    private Class<? extends JkAbstractArgs> argsClass;

    public ParserArgs(Class<? extends JkAbstractArgs> argsClass, ParserNames parserNames, boolean checkDesign) {
        this.argsClass = argsClass;
    }
}
