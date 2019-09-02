package xxx.joker.libs.argsparser.service;

import xxx.joker.libs.argsparser.design.classTypes.JkArgsTypes;
import xxx.joker.libs.argsparser.design.classTypes.JkCommands;
import xxx.joker.libs.argsparser.design.descriptors.COption;
import xxx.joker.libs.argsparser.design.descriptors.CParam;
import xxx.joker.libs.core.lambdas.JkStreams;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by f.barbano on 30/08/2017.
 */

public class CmdWrapper {

	private JkCommands cmd;
	private List<String> evolutions;
	private int numOfIndependentEvolutions;

	public CmdWrapper(JkCommands cmd) {
		this.cmd = cmd;
		this.evolutions = new ArrayList<>();
		this.numOfIndependentEvolutions = -1;
	}

	public int countIndependentEvolutions() {
		if(numOfIndependentEvolutions == -1) {
			List<CParam> noDeps = JkStreams.filter(getParams(), cpar -> cpar.getDependFrom() == null);

			int count = 0;
			int evolNum = 1;

			// Analyze only params without option dependency
			for (CParam cp : noDeps) {
				int num = cp.getOptions().size() * evolNum;
				count += num * (cp.isRequired() ? 1 : 2);
				if (count < 0) return Integer.MAX_VALUE;
				evolNum = count;
			}

			numOfIndependentEvolutions = count;
		}

		return numOfIndependentEvolutions;
	}

	public List<CParam> getParams() {
		return cmd.params();
	}

	public List<COption> getOptions() {
		return getParams().stream()
                .flatMap(cp -> cp.getOptions().stream())
                .collect(Collectors.toList());
	}

	public COption getOption(JkArgsTypes argType) {
		return JkStreams.findUnique(getOptions(), cp -> cp.getArgType() == argType);
	}

	public List<String> getEvolutions() {
		return evolutions;
	}

	public void setEvolutions(List<String> evolutions) {
		this.evolutions = evolutions;
	}

	public JkCommands getCmd() {
		return cmd;
	}

	public String getCmdName() {
		return cmd.name();
	}

	public Enum<? extends JkCommands> getCmdEnum() {
		return (Enum<? extends JkCommands>) cmd;
	}
}
