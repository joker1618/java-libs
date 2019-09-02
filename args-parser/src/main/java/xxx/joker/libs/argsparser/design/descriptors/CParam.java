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
	private Enum<? extends JkArgsTypes> dependFrom;
	private List<COption> options;

	public CParam(COption... options) {
		this(true, options);
	}

	public CParam(boolean required, COption... options) {
		this(required, null, null, options);
	}

	public CParam(Enum<? extends JkArgsTypes> _default, COption... options) {
		this(false, _default, null, options);
	}

	public CParam(Enum<? extends JkArgsTypes> _default, Enum<? extends JkArgsTypes> dependFrom, COption... options) {
		this(false, _default, dependFrom, options);
	}

	public CParam(boolean required, Enum<? extends JkArgsTypes> dependFrom, COption... options) {
		this(true, null, dependFrom, options);
	}

	private CParam(boolean required, Enum<? extends JkArgsTypes> _default, Enum<? extends JkArgsTypes> dependFrom, COption... options) {
		this.required = required;
		this._default = _default;
		this.dependFrom = dependFrom;
		this.options = JkConvert.toList(options);
	}


	public boolean isRequired() {
		return required;
	}

	public JkArgsTypes getDefault() {
		return (JkArgsTypes)_default;
	}
	public Enum<? extends JkArgsTypes> getDefaultEnum() {
		return _default;
	}

	public Enum<? extends JkArgsTypes> getDependFrom() {
		return dependFrom;
	}

	public List<COption> getOptions() {
		return options;
	}
}
