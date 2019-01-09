package xxx.joker.libs.oldargsparser.service;

/**
 * Created by f.barbano on 03/09/2017.
 */
import xxx.joker.libs.core.ToAnalyze;

@ToAnalyze
@Deprecated
public interface ICmdService {

	CmdWrapper getByEvolution(String evolution);

}
