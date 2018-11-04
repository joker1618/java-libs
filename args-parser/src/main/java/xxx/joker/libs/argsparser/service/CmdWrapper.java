package xxx.joker.libs.argsparser.service;

import xxx.joker.libs.argsparser.design.classType.InputCommand;
import xxx.joker.libs.argsparser.design.classType.OptionName;
import xxx.joker.libs.argsparser.model.CmdOption;
import xxx.joker.libs.argsparser.model.CmdParam;
import xxx.joker.libs.core.utils.JkStreams;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by f.barbano on 30/08/2017.
 */
public class CmdWrapper {

	private InputCommand cmd;
	private int numOfIndependentEvolutions;

	public CmdWrapper(InputCommand cmd) {
		this.cmd = cmd;
		this.numOfIndependentEvolutions = -1;
	}

	public int countIndependentEvolutions() {
		if(numOfIndependentEvolutions == -1) {
			List<CmdParam> noDeps = JkStreams.filter(getParamList(), cpar -> cpar.getDependOn() == null);

			int count = 0;
			int evolNum = 1;

			// Analyze only params without option dependency
			for (CmdParam cp : noDeps) {
				int num = cp.getOptionList().size() * evolNum;
				count += num * (cp.isRequired() ? 1 : 2);
				if (count < 0) return Integer.MAX_VALUE;
				evolNum = count;
			}

			numOfIndependentEvolutions = count;
		}

		return numOfIndependentEvolutions;
	}

	public List<CmdParam> getParamList() {
		return cmd.paramList();
	}

	public List<CmdOption> getOptionList() {
		return getParamList().stream().flatMap(cp -> cp.getOptionList().stream()).collect(Collectors.toList());
	}

	public CmdOption getOption(OptionName optName) {
		return getOptionList().stream().filter(co -> co.getOption().name().equals(optName.name())).findAny().orElse(null);
	}

	public String getName() {
		return cmd.name();
	}

	public Enum<? extends InputCommand> getCmdEnum() {
		return (Enum<? extends InputCommand>) cmd;
	}
}
