package xxx.joker.libs.argsparser.service;

import xxx.joker.libs.argsparser.design.classTypes.JkCommands;
import xxx.joker.libs.argsparser.design.descriptors.COption;
import xxx.joker.libs.argsparser.design.descriptors.CParam;
import xxx.joker.libs.core.utils.JkStreams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by f.barbano on 30/08/2017.
 */

public class CmdWrapper {

	private JkCommands cmd;
	private List<String> evolutions;

	public CmdWrapper(JkCommands cmd) {
		this.cmd = cmd;
		this.evolutions = new ArrayList<>();
	}

	public List<CParam> getParams() {
		return cmd.params();
	}

	public List<COption> getOptions() {
		return getParams().stream()
                .flatMap(cp -> cp.getOptions().stream())
                .collect(Collectors.toList());
	}

	public List<String> getEvolutions() {
		return evolutions;
	}

	public void setEvolutions(Collection<String> evolutions) {
		this.evolutions.clear();
		this.evolutions.addAll(evolutions);
	}

	public String getCmdName() {
		return cmd.name();
	}

	public Enum<? extends JkCommands> getCmdEnum() {
		return (Enum<? extends JkCommands>) cmd;
	}
}
