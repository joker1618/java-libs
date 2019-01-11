package xxx.joker.libs.argsparser.service;

import java.util.Collection;

public interface DesignService {

    ArgWrapper getArgByNameAlias(String nameOrAlias);

    CmdWrapper retrieveCommand(Collection<ArgWrapper> inputArgs);

}
