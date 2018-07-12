package xxx.joker.libs.javalibs.dao.csv;

import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.javalibs.utils.*;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static xxx.joker.libs.javalibs.utils.JkConsole.display;
import static xxx.joker.libs.javalibs.utils.JkStrings.strf;

public class JkCsvDaoNew<T extends CsvElement> {

	private static final String LINE_SEP = StringUtils.LF;
	private static final CsvSep FIELD_SEP = new CsvSep(";", ";");
	private static final CsvSep LIST_SEP = new CsvSep(",", ",");
//	private static final CsvSep FIELD_SEP = new CsvSep("###FIELD_SEP###", ";");
//	private static final CsvSep LIST_SEP = new CsvSep("###LIST_SEP###", ",");

	private static final String PLACEHOLDER_TAB = "##TAB##";
	private static final String PLACEHOLDER_NEWLINE = "##NEWLINE##";

	private Path csvPath;
	private Path depsPath;
	private Class<T> csvClass;
	private Map<Integer,AnnField> fieldMap;
	private Map<Class<?>,JkCsvDaoNew> daoParsers;
	private int numFields;

	public JkCsvDaoNew(Path csvPath, Class<T> csvClass) {
		this.csvPath = csvPath;
		this.depsPath = JkFiles.getParent(csvPath).resolve(strf("%s.dependencies.%s", JkFiles.getFileName(csvPath), JkFiles.getExtension(csvPath)));
		this.csvClass = csvClass;
		this.fieldMap = new HashMap<>();
		this.daoParsers = new HashMap<>();
		initialize();
	}

	private JkCsvDaoNew(Class<T> csvClass) {
		this.csvClass = csvClass;
		this.fieldMap = new HashMap<>();
		this.daoParsers = new HashMap<>();
		initialize();
	}

	public List<T> readAll() throws Exception {
		if(!Files.exists(csvPath)) {
			return Collections.emptyList();
		}

		Map<String, String> depsMap = JkStreams.toMapSingle(Files.readAllLines(depsPath), l -> l.split(":")[0], l -> l.split(":")[1]);
		List<String> lines = Files.readAllLines(csvPath);

		return JkStreams.map(lines, line -> parseElem(line, depsMap));
	}

	private T parseElem(String line, Map<String, String> depsMap) {
		try {
			T instance = csvClass.newInstance();
			List<String> row = JkStrings.splitFieldsList(line, FIELD_SEP.safeSep);
			for(Map.Entry<Integer,AnnField> entry : fieldMap.entrySet()) {
				Object o = fromStringValue(row.get(entry.getKey()), entry.getValue(), depsMap);
				entry.getValue().setValue(instance, o);
			}
			return instance;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void persist(List<T> elems) throws Exception {
		List<StringCSV> csvList = JkStreams.map(elems, this::formatElem);
		List<String> mainLines = JkStreams.map(csvList, StringCSV::getMainValue);
		List<String> depLines = csvList.stream().flatMap(csv -> csv.getDependencies().stream())
									.sorted().distinct().collect(Collectors.toList());

		JkFiles.writeFile(csvPath, mainLines, true);
		JkFiles.writeFile(depsPath, depLines, true);
	}

	private StringCSV formatElem(CsvElement elem) {
		StringCSV toRet = new StringCSV();

		try {
			// Parse instance
			JkCsvDaoNew daoParser = daoParsers.get(elem.getClass());
			List<String> deps = new ArrayList<>();
			List<String> row = Stream.generate(() -> "").limit(daoParser.numFields).collect(Collectors.toList());
			for (Object key : daoParser.fieldMap.keySet()) {
				AnnField annField = (AnnField) daoParser.fieldMap.get(key);
				Object value = annField.getValue(elem);
				StringCSV str = daoParser.toStringValue(value, annField);
				row.set((Integer) key, str.mainValue);
				deps.addAll(str.dependencies);
			}

			toRet.mainValue = JkStreams.join(row, FIELD_SEP.safeSep);
			toRet.dependencies = deps;

			return toRet;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void initialize() {
		List<Field> fields = JkReflection.getFieldsByAnnotation(csvClass, CsvField.class);

		numFields = fields.stream().map(f -> f.getAnnotation(CsvField.class)).mapToInt(CsvField::index).max().orElse(-1) + 1;
		daoParsers.putIfAbsent(csvClass, this);

		if(!fields.isEmpty()) {
			for (Field f : fields) {
				CsvField csvField = f.getAnnotation(CsvField.class);
				if (csvField.index() < 0) {
					throw new IllegalArgumentException(strf("Negative index not allowed. Field %s", f.getName()));
				}
				if (fieldMap.containsKey(csvField.index())) {
					throw new IllegalArgumentException(strf("Duplicated index %d", csvField.index()));
				}
				if(isCsvElementClass(f.getType())) {
					Class<? extends CsvElement> ftype = (Class<? extends CsvElement>)f.getType();
					daoParsers.putIfAbsent(ftype, new JkCsvDaoNew<>(ftype));
				}
				fieldMap.put(csvField.index(), new AnnField(csvField, f));
			}
		}
	}
	
	private boolean isCsvElementClass(Class<?> clazz) {
		List<Class<?>> interfaces = Arrays.asList(clazz.getInterfaces());
		return interfaces.contains(CsvElement.class);
	}

	private String getCsvElementsPrefix(CsvElement elem) {
		return strf("%s_%s", elem.getClassHash(), elem.getElemID());
	}

	private StringCSV toStringValue(Object value, AnnField annField) {
		StringCSV retVal = new StringCSV();

		Class<?> fclazz = annField.field.getType();
		if(fclazz == List.class) {
			Class<?> elemClazz = annField.ann.listElemType();
			if(value != null) {
				List list = (List) value;
				if(!list.isEmpty()) {
					List<StringCSV> csvList = JkStreams.map(list, e -> toStringSingleValue(e, elemClazz));
					retVal.mainValue = JkStreams.join(csvList, LIST_SEP.safeSep, StringCSV::getMainValue);
					retVal.dependencies = csvList.stream().flatMap(c -> c.getDependencies().stream()).collect(Collectors.toList());
				}
			}
		} else {
			retVal = toStringSingleValue(value, fclazz);
		}

		return retVal;
	}

	private StringCSV toStringSingleValue(Object value, Class<?> fclazz) {
		try {
			StringCSV str = new StringCSV();

			if (value == null) {
				str.mainValue = "";
			} else if (Arrays.asList(boolean.class, Boolean.class).contains(fclazz)) {
				str.mainValue = ((Boolean) value) ? "true" : "false";
			} else if (Arrays.asList(File.class, Path.class).contains(fclazz)) {
				str.mainValue = value.toString();
			} else if (fclazz == LocalTime.class) {
				str.mainValue = DateTimeFormatter.ISO_TIME.format((LocalTime) value);
			} else if (fclazz == LocalDate.class) {
				str.mainValue = DateTimeFormatter.ISO_DATE.format((LocalDate) value);
			} else if (fclazz == LocalDateTime.class) {
				str.mainValue = DateTimeFormatter.ISO_DATE_TIME.format((LocalDateTime) value);
			} else if (isCsvElementClass(fclazz)) {
				CsvElement cel = (CsvElement) value;
				str.mainValue = cel.getElemID();
				StringCSV csv = formatElem(cel);
				String ml = strf("%s:%s", getCsvElementsPrefix(cel), csv.mainValue);
				str.dependencies.add(ml);
				str.dependencies.addAll(csv.dependencies);
			} else {
				str.mainValue = String.valueOf(value).replaceAll("\t", PLACEHOLDER_TAB).replaceAll("\n", PLACEHOLDER_NEWLINE);
			}

			return str;

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private Object fromStringValue(String value, AnnField annField, Map<String, String> depMap) {
		Object retVal;

		Class<?> fclazz = annField.field.getType();
		if(fclazz == List.class) {
			List<String> strElems = JkStrings.splitFieldsList(value, LIST_SEP.safeSep);
			Class<?> elemClazz = annField.ann.listElemType();
			retVal = JkStreams.map(strElems, elem -> fromStringSingleValue(elem, elemClazz, depMap));
		} else {
			retVal = fromStringSingleValue(value, fclazz, depMap);
		}

		return retVal;
	}

	private Object fromStringSingleValue(String value, Class<?> fclazz, Map<String, String> depMap) {
		Object o;

		try {
			if (StringUtils.isEmpty(value)) {
				o = fclazz == String.class ? "" : null;
			} else if (Arrays.asList(boolean.class, Boolean.class).contains(fclazz)) {
				o = Boolean.valueOf(value);
			} else if (Arrays.asList(boolean.class, Boolean.class).contains(fclazz)) {
				o = Boolean.valueOf(value);
			} else if (Arrays.asList(int.class, Integer.class).contains(fclazz)) {
				o = JkConverter.stringToInteger(value);
			} else if (Arrays.asList(int.class, Integer.class).contains(fclazz)) {
				o = JkConverter.stringToInteger(value);
			} else if (Arrays.asList(long.class, Long.class).contains(fclazz)) {
				o = JkConverter.stringToLong(value);
			} else if (Arrays.asList(long.class, Long.class).contains(fclazz)) {
				o = JkConverter.stringToLong(value);
			} else if (Arrays.asList(double.class, Double.class).contains(fclazz)) {
				o = JkConverter.stringToDouble(value);
			} else if (Arrays.asList(double.class, Double.class).contains(fclazz)) {
				o = JkConverter.stringToDouble(value);
			} else if (Arrays.asList(float.class, Float.class).contains(fclazz)) {
				o = JkConverter.stringToFloat(value);
			} else if (Arrays.asList(float.class, Float.class).contains(fclazz)) {
				o = JkConverter.stringToFloat(value);
			} else if (fclazz == Path.class) {
				o = Paths.get(value);
			} else if (fclazz == File.class) {
				o = new File(value);
			} else if (fclazz == LocalTime.class) {
				o = LocalTime.parse(value, DateTimeFormatter.ISO_TIME);
			} else if (fclazz == LocalDate.class) {
				o = LocalDate.parse(value, DateTimeFormatter.ISO_DATE);
			} else if (fclazz == LocalDateTime.class) {
				o = LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
			} else if (isCsvElementClass(fclazz)) {
				CsvElement cel = (CsvElement) fclazz.newInstance();
				cel.setElemID(value);
				String depLine = depMap.get(getCsvElementsPrefix(cel));
				JkCsvDaoNew daop = daoParsers.get(fclazz);
				o = daop.parseElem(depLine, depMap);
			} else {
				o = value.replaceAll(PLACEHOLDER_TAB, "\t").replaceAll(PLACEHOLDER_NEWLINE, "\n");
			}

			return o;

		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}



	private static class AnnField {
		CsvField ann;
		Field field;

		AnnField(CsvField ann, Field field) {
			this.ann = ann;
			this.field = field;
		}

		Object getValue(Object elem) throws IllegalAccessException {
			boolean facc = field.isAccessible();
			field.setAccessible(true);
			Object obj = field.get(elem);
			field.setAccessible(facc);
			return obj;
		}

		void setValue(Object elem, Object value) throws IllegalAccessException {
			boolean facc = field.isAccessible();
			field.setAccessible(true);
			field.set(elem, value);
			field.setAccessible(facc);
		}
	}

	private static class CsvSep {
		String safeSep;
		String simpleSep;

		CsvSep(String safeSep, String simpleSep) {
			this.safeSep = safeSep;
			this.simpleSep = simpleSep;
		}
	}

	private static class StringCSV {
		String mainValue = "";
		List<String> dependencies = new ArrayList<>();

		public String getMainValue() {
			return mainValue;
		}

		public void setMainValue(String mainValue) {
			this.mainValue = mainValue;
		}

		public List<String> getDependencies() {
			return dependencies;
		}

		public void setDependencies(List<String> dependencies) {
			this.dependencies = dependencies;
		}
	}

}
