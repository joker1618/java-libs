package xxx.joker.libs.argsparser.design.descriptors;

import xxx.joker.libs.argsparser.design.classTypes.JkArgsTypes;
import xxx.joker.libs.core.utils.JkConvert;

import java.util.List;

/**
 * Created by f.barbano on 26/08/2017.
 */

public class CParam {

	private boolean required;
	private Enum<? extends JkArgsTypes> _default;
	private Enum<? extends JkArgsTypes> dependOn;
	private List<COption> options;

	public CParam(COption... options) {
		this(true, options);
	}

	public CParam(boolean required, COption... options) {
		this(null, null, required, options);
	}

	public CParam(Enum<? extends JkArgsTypes> _default, Enum<? extends JkArgsTypes> dependOn, boolean required, COption... options) {
		this.required = required;
		this._default = _default;
		this.dependOn = dependOn;
		this.options = JkConvert.toArrayList(options);
	}

	public boolean isRequired() {
		return required;
	}

	public Enum<? extends JkArgsTypes> getDefault() {
		return _default;
	}

	public Enum<? extends JkArgsTypes> getDependOn() {
		return dependOn;
	}

	public List<COption> getOptions() {
		return options;
	}
}
