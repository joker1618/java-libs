package xxx.joker.libs.core.config;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.core.utils.JkStreams;
import xxx.joker.libs.core.utils.JkStrings;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static xxx.joker.libs.core.utils.JkStrings.strf;

/**
 * Created by f.barbano on 12/10/2017.
 */

public abstract class JkAbstractConfigs {

	protected Map<String, Prop> configMap;

	private static final String KEY_SEP = "=";
	private static final String COMMENT_START = "#";


	protected JkAbstractConfigs() {
		this.configMap = Collections.synchronizedMap(new HashMap<>());
	}

	protected void loadConfigFile(Path configFile) {
		if(Files.exists(configFile)) {
			List<String> lines = JkFiles.readLines(configFile);
			addPropertiesFromFileLines(lines);
		}
	}
	protected void loadConfigFile(String configFilePath) {
		loadConfigFile(Paths.get(configFilePath));
	}
	protected void loadConfigFile(InputStream is) {
		List<String> lines = JkFiles.readLines(is);
		addPropertiesFromFileLines(lines);
	}
	private void addPropertiesFromFileLines(List<String> lines) {
		// read properties from file
		List<String> tmp = JkStreams.map(lines, String::trim);
		tmp.removeIf(StringUtils::isBlank);
		tmp.removeIf(line -> line.startsWith(COMMENT_START));
		tmp.removeIf(line -> !line.contains(KEY_SEP));

		for(String line : tmp) {
			int idxSep = line.indexOf(KEY_SEP);
			String key = line.substring(0, idxSep).trim();
			String value = line.substring(idxSep+1).trim();
			configMap.put(key, new Prop(key, value, value));
		}

		// replace environment variables  ${env:var}
		evaluateEnvironmentVariables();

		// replace variables
		// #var#  and  ${var}  allowed
		evaluateVariables();
	}

	protected void persist(Path outputPath) {
		List<String> lines = JkStreams.map(configMap.values(), p -> strf("%s=%s", p.key, p.evalutedValue));
		JkFiles.writeFile(outputPath, lines, true);
	}

	protected String getString(String key) {
		return getString(key, null);
	}
	protected String getString(String key, String _default) {
		Prop prop = configMap.get(key);
		return prop == null ? _default : prop.evalutedValue;
	}

	protected Integer getInt(String key) {
		return getInt(key, null);
	}
	protected Integer getInt(String key, Integer _default) {
		return JkConvert.toInt(getString(key), _default);
	}

	protected Long getLong(String key) {
		return getLong(key, null);
	}
	protected Long getLong(String key, Long _default) {
		return JkConvert.toLong(getString(key), _default);
	}

	protected Double getDouble(String key) {
		return getDouble(key, null);
	}
	protected Double getDouble(String key, Double _default) {
		return JkConvert.toDouble(getString(key), _default);
	}

	protected BigDecimal getBigDecimal(String key) {
		return getBigDecimal(key, null);
	}
	protected BigDecimal getBigDecimal(String key, BigDecimal _default) {
		String value = getString(key);
		if(value == null)	return _default;
		Double d = JkConvert.toDouble(value);
		return d == null ? _default : BigDecimal.valueOf(d);
	}

	protected Path getPath(String key) {
		return Paths.get(getString(key));
	}
	protected Path getPath(String key, Path _default) {
		String value = getString(key);
		return value == null ? _default : Paths.get(value);
	}

	protected boolean getBoolean(String key) {
		return Boolean.valueOf(getString(key));
	}
	protected boolean getBoolean(String key, boolean def) {
		String str = getString(key);
		return StringUtils.isBlank(str) ? def : Boolean.valueOf(str);
	}
	
	protected void addProperty(String key, String value) {
		Prop prop = new Prop(key, value, value);
		configMap.put(key, prop);
		evaluateVariables();
	}

	protected boolean containsKey(String key) {
		return configMap.containsKey(key);
	}

	@Override
	public String toString() {
		List<String> list = new ArrayList<>();
		configMap.forEach((k,v) -> list.add(k + "=" + v.evalutedValue));
		return list.stream().collect(Collectors.joining("\n"));
	}


	private void evaluateEnvironmentVariables() {
		for(Prop prop : configMap.values()) {
			String[] envVars = StringUtils.substringsBetween(prop.originalValue, "${env:", "}");
			if(envVars != null) {
				String fixed = prop.originalValue;
				for(String evar : envVars) {
					String strPh = "${env:" + evar + "}";
					String strRepl = JkStrings.safeTrim(System.getProperty(evar));
					fixed = fixed.replace(strPh, strRepl);
				}
				prop.originalValue = fixed;
			}
		}
	}
	private void evaluateVariables() {
		configMap.forEach((key,prop) -> prop.evalutedValue = prop.originalValue);

		Set<String> keys = configMap.keySet();
		boolean changed;
		do {
			changed = false;
			for(String key : keys) {
				Prop prop = configMap.get(key);
				String actualEval = prop.evalutedValue;
				if(containsVariables(actualEval)) {
					List<Var> vars = getVariables(actualEval);
					String newEval = actualEval;
					for (Var v : vars) {
						Prop p = configMap.get(v.varName);
						if(p != null) 	newEval = newEval.replace(v.placeholder, p.evalutedValue);

					}
					if(!newEval.equals(actualEval)) {
						prop.evalutedValue = newEval;
						changed = true;
					}
				}
			}
		} while (changed);
	}
	// return the next variable found:   #var#  or  ${var}
	private List<Var> getVariables(String value) {
		String str = value;
		String varName;
		boolean go = true;
		List<Var> toRet = new ArrayList<>();

		while(go) {
			Var var = null;

			varName = StringUtils.substringBetween(str, "#", "#");
			if(StringUtils.isNotBlank(varName)) {
				var = new Var(varName, "#" + varName + "#");
			} else {
				varName = StringUtils.substringBetween(str, "${", "}");
				if(StringUtils.isNotBlank(varName)) {
					var = new Var(varName, "${" + varName + "}");
				}
			}

			if(var == null) {
				go = false;
			} else {
				toRet.add(var);
				int nextStart = str.indexOf(var.placeholder) + var.placeholder.length();
				str = str.substring(nextStart);
			}
		}

		return toRet;
	}
	private boolean containsVariables(String value) {
		String varName = StringUtils.substringBetween(value, "#", "#");
		if(StringUtils.isNotBlank(varName)) {
			return true;
		}

		varName = StringUtils.substringBetween(value, "${", "}");
		if(StringUtils.isNotBlank(varName)) {
			return true;
		}

		return false;
	}

	private class Var {
		String varName;
		String placeholder;
		Var(String varName, String placeholder) {
			this.varName = varName;
			this.placeholder = placeholder;
		}
	}

	protected class Prop {
		String key;
		String originalValue;
		String evalutedValue;
		Prop(String key, String originalValue, String evalutedValue) {
			this.key = key;
			this.originalValue = originalValue;
			this.evalutedValue = evalutedValue;
		}
		public String toString() {
			return String.format("[%s, %s]", originalValue, evalutedValue);
		}

		public String getKey() {
			return key;
		}

		public String getOriginalValue() {
			return originalValue;
		}

		public String getEvalutedValue() {
			return evalutedValue;
		}
	}
}

