package xxx.joker.libs.argsparser.model;

import xxx.joker.libs.argsparser.design.classType.OptionName;
import xxx.joker.libs.core.utils.JkConverter;

import java.util.List;

/**
 * Created by f.barbano on 26/08/2017.
 */
import xxx.joker.libs.core.ToAnalyze;

@ToAnalyze
public class CmdParam {

	private boolean required;
	private Enum<? extends OptionName> dependOn;
	private List<CmdOption> optionList;

	public CmdParam(CmdOption... options) {
		this(null, true, options);
	}

	public CmdParam(boolean required, CmdOption... options) {
		this(null, required, options);
	}

	public CmdParam(Enum<? extends OptionName> dependOn, boolean required, CmdOption... options) {
		this.required = required;
		this.dependOn = dependOn;
		this.optionList = JkConverter.toArrayList(options);
	}

	public boolean isRequired() {
		return required;
	}

	public Enum<? extends OptionName> getDependOn() {
		return dependOn;
	}

	public List<CmdOption> getOptionList() {
		return optionList;
	}
}
