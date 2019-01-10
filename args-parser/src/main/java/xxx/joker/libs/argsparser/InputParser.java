package xxx.joker.libs.argsparser;

import xxx.joker.libs.argsparser.design.classTypes.JkAbstractArgs;
import xxx.joker.libs.argsparser.exceptions.ParseError;

public interface InputParser {


    <T extends JkAbstractArgs> T parse(String inputLine) throws ParseError;
    <T extends JkAbstractArgs> T parse(String[] inputArgs) throws ParseError;

}
