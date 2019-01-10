package xxx.joker.libs.argsparser.service;

import xxx.joker.libs.argsparser.design.classTypes.JkArgsNames;

class ParserNames {

    private Class<? extends JkArgsNames> argsNamesClass;

    public ParserNames(Class<? extends JkArgsNames> argsNamesClass, boolean checkDesign) {
        this.argsNamesClass = argsNamesClass;
    }
}
