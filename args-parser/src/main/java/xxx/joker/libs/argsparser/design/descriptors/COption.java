package xxx.joker.libs.argsparser.design.descriptors;

import xxx.joker.libs.argsparser.design.classTypes.JkArgsTypes;
import xxx.joker.libs.argsparser.exceptions.DesignError;
import xxx.joker.libs.core.datetime.JkTimes;
import xxx.joker.libs.core.utils.JkConvert;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Created by f.barbano on 26/08/2017.
 */

public class COption {

	private Enum<? extends JkArgsTypes> argType;
	private Class<?> optionClass;
	private UnaryOperator<String[]> transformBefore;  // value transformation before parse to correct class
	private UnaryOperator<Object[]> transformAfter;   // value transformation after parse to correct class
	private List<Function<String[], String>> checksBefore;
	private List<Function<String[], String>> checksMiddle;
	private List<Function<Object[], String>> checksAfter;
	private DateTimeFormatter dateTimeFormatter;
	
	protected COption(Enum<? extends JkArgsTypes> argType) {
		this.argType = argType;
		this.checksBefore = new ArrayList<>();
		this.checksMiddle = new ArrayList<>();
		this.checksAfter = new ArrayList<>();
	}

	public static COption ofDate(Enum<? extends JkArgsTypes> argType, String dateFormat) {
		return new COption(argType).setOptionClass(LocalDate.class).setDateTimeFormatter(dateFormat);
	}
	public static COption ofTime(Enum<? extends JkArgsTypes> argType, String timeFormat) {
		return new COption(argType).setOptionClass(LocalTime.class).setDateTimeFormatter(timeFormat);
	}
	public static COption ofDateTime(Enum<? extends JkArgsTypes> argType, String dateTimeFormat) {
		return new COption(argType).setOptionClass(LocalDateTime.class).setDateTimeFormatter(dateTimeFormat);
	}

	public static COption ofPathWindows(Enum<? extends JkArgsTypes> argType) {
		return new COption(argType).setOptionClass(Path.class)
				.setTransformBefore(JkConvert::unixToWinPath);
	}
	public static COption ofPathsWindows(Enum<? extends JkArgsTypes> argType) {
		return new COption(argType).setOptionClass(Path[].class)
				.setTransformBefore(JkConvert::unixToWinPath);
	}

	public String getArgName() {
		return ((JkArgsTypes)argType).getArgName();
	}


	public Enum<? extends JkArgsTypes> getArgType() {
		return argType;
	}
	public COption setArgType(Enum<? extends JkArgsTypes> argType) {
		this.argType = argType;
		return this;
	}

	public Class<?> getArgClass() {
		return optionClass;
	}
	public COption setOptionClass(Class<?> optionClass) {
		this.optionClass = optionClass;
		return this;
	}

	public UnaryOperator<String[]> getTransformBefore() {
		return transformBefore;
	}
	public COption setTransformBeforeAll(UnaryOperator<String[]> transformBefore) {
		this.transformBefore = transformBefore;
		return this;
	}
	public COption setTransformBefore(UnaryOperator<String> transformBefore) {
		this.transformBefore = arr -> {
			String[] newArr = new String[arr.length];
			for(int i = 0; i < arr.length; i++) {
				newArr[i] = transformBefore.apply(arr[i]);
			}
			return newArr;
		};
		return this;
	}

	public UnaryOperator<Object[]> getTransformAfter() {
		return transformAfter;
	}
	public COption setTransformAfterAll(UnaryOperator<Object[]> transformAfter) {
		this.transformAfter = transformAfter;
		return this;
	}
	public COption setTransformAfter(UnaryOperator<Object> transformAfter) {
		this.transformAfter = arr -> {
			Object[] newArr = new Object[arr.length];
			for(int i = 0; i < arr.length; i++) {
				newArr[i] = transformAfter.apply(arr[i]);
			}
			return newArr;
		};
		return this;
	}

	public List<Function<String[], String>> getChecksBefore() {
		return checksBefore;
	}
	public COption addChecksBefore(Function<String, String>... checksBefore) {
		for(Function<String, String> func : checksBefore) {
			Function<String[], String> funcAll = arr -> {
				for(int i = 0; i < arr.length; i++) {
					String mex = func.apply(arr[i]);
					if(mex != null) {
						return mex;
					}
				}
				return null;
			};
			addChecksBeforeAll(funcAll);
		}
		return this;
	}
	@SafeVarargs
	public final COption addChecksBeforeAll(Function<String[], String>... checksBefore) {
		this.checksBefore = JkConvert.toArrayList(checksBefore);
		return this;
	}

	public List<Function<String[], String>> getChecksMiddle() {
		return checksMiddle;
	}
	public COption addChecksMiddle(Function<String, String>... checksMiddle) {
		for(Function<String, String> func : checksMiddle) {
			Function<String[], String> funcAll = arr -> {
				for(int i = 0; i < arr.length; i++) {
					String mex = func.apply(arr[i]);
					if(mex != null) {
						return mex;
					}
				}
				return null;
			};
			addChecksMiddleAll(funcAll);
		}
		return this;
	}
	@SafeVarargs
	public final COption addChecksMiddleAll(Function<String[], String>... checksMiddle) {
		this.checksMiddle = JkConvert.toArrayList(checksMiddle);
		return this;
	}

	public List<Function<Object[], String>> getChecksAfter() {
		return checksAfter;
	}
	public COption addChecksAfter(Function<Object, String>... checksAfter) {
		for(Function<Object, String> func : checksAfter) {
			Function<Object[], String> funcAll = arr -> {
				for(int i = 0; i < arr.length; i++) {
					String mex = func.apply(arr[i]);
					if(mex != null) {
						return mex;
					}
				}
				return null;
			};
			addChecksAfterAll(funcAll);
		}
		return this;
	}
	@SafeVarargs
	public final COption addChecksAfterAll(Function<Object[], String>... checksAfter) {
		this.checksAfter = JkConvert.toArrayList(checksAfter);
		return this;
	}

	public DateTimeFormatter getDateTimeFormatter() {
		return dateTimeFormatter;
	}
	public COption setDateTimeFormatter(String dateTimeFormat) {
		if(!JkTimes.isValidDateTimeFormatter(dateTimeFormat)) {
			throw new DesignError(COption.class, "arg {}, wrong datetime format [{}]", argType, dateTimeFormat);
		}
		this.dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimeFormat);
		return this;
	}
	public COption setDateTimeFormatter(DateTimeFormatter dateTimeFormatter) {
		this.dateTimeFormatter = dateTimeFormatter;
		return this;
	}
}
