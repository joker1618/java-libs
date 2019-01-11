package xxx.joker.libs.argsparser.service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface DesignService {

    ArgWrapper getArgByNameAlias(String nameOrAlias);

    CmdWrapper retrieveCommand(Collection<ArgWrapper> inputArgs);

}
