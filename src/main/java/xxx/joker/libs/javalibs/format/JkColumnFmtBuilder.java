package xxx.joker.libs.javalibs.format;

import javafx.scene.text.TextAlignment;
import org.apache.commons.lang3.StringUtils;
import xxx.joker.libs.javalibs.utils.JkStreams;
import xxx.joker.libs.javalibs.utils.JkStrings;

import java.util.*;

import static xxx.joker.libs.javalibs.utils.JkStrings.strf;

/**
 * Created by f.barbano on 26/05/2018.
 */
public class JkColumnFmtBuilder {

	private List<String> lines;
	private TextAlignment headerAlign;
	private TextAlignment dataAlign;
	private Map<Integer,TextAlignment> headerFieldAlign;
	private Map<Integer,TextAlignment> columnFieldAlign;

	public JkColumnFmtBuilder() {
		this(new ArrayList<>());
	}
	public JkColumnFmtBuilder(List<String> lines) {
		this.lines = lines;
		this.headerAlign = null;
		this.dataAlign = TextAlignment.LEFT;
		this.headerFieldAlign = new HashMap<>();
		this.columnFieldAlign = new HashMap<>();
	}

	public JkColumnFmtBuilder addLines(String source) {
		lines.addAll(JkStrings.splitFieldsList(source, StringUtils.LF));
		return this;
	}
	public JkColumnFmtBuilder addLines(int index, String source) {
		lines.addAll(index, JkStrings.splitFieldsList(source, StringUtils.LF));
		return this;
	}
	public JkColumnFmtBuilder addLines(List<String> sourceLines) {
		lines.addAll(sourceLines);
		return this;
	}
	public JkColumnFmtBuilder addLines(int index, List<String> sourceLines) {
		lines.addAll(index, sourceLines);
		return this;
	}

	public JkColumnFmtBuilder setHeaderAlign(TextAlignment align, Integer... columnIndexes) {
		if(columnIndexes.length == 0) {
			headerAlign = align;
		} else {
			Arrays.stream(columnIndexes).forEach(i -> headerFieldAlign.put(i, align));
		}
		return this;
	}
	public JkColumnFmtBuilder setColumnAlign(TextAlignment align, Integer... columnIndexes) {
		if(columnIndexes.length == 0) {
			dataAlign = align;
		} else {
			Arrays.stream(columnIndexes).forEach(i -> columnFieldAlign.put(i, align));
		}
		return this;
	}

	public String toString(String separator, int columnDistance) {
		return toString(separator, columnDistance, false);
	}
	public String toString(String separator, int columnDistance, boolean trimValues) {
		return JkStreams.join(toLines(separator, columnDistance, trimValues), StringUtils.LF);
	}

	public List<String> toLines(String separator, int columnDistance) {
		return toLines(separator, columnDistance, false);
	}
	public List<String> toLines(String separator, int columnDistance, boolean trimValues) {
		if(columnDistance < 0) {
			columnDistance = 0;
		}

		// Split lines in fields
		List<String[]> fieldLines = JkStreams.map(lines, line -> JkStrings.splitAllFields(line, separator, trimValues));

		// Compute column descriptors
		ColDescr[] columnDescrs = computeColumnDescriptors(fieldLines);

		// create the view
		List<String> lines = new ArrayList<>();

		String columnSep = StringUtils.repeat(" ", columnDistance);
		for(int row = 0; row < fieldLines.size(); row++) {
			List<String> fields = new ArrayList<>();
			for(int col = 0; col < columnDescrs.length; col++) {
				String str = col < fieldLines.get(row).length ? fieldLines.get(row)[col] : "";
				String field = columnDescrs[col].formatText(str, row == 0);
				fields.add(field);
			}
			lines.add(JkStreams.join(fields, columnSep));
		}

		return lines;
	}

	public void insertPrefix(String prefix) {
		lines = JkStreams.map(lines, l -> strf("%s%s", prefix, l));
	}

	private ColDescr[] computeColumnDescriptors(List<String[]> fieldLines) {
		// Retrieve max number of fields for one line
		int columnNumber = fieldLines.stream().mapToInt(sarr -> sarr.length).max().orElse(0);

		// Compute descriptor for each column
		ColDescr[] colDescrs = new ColDescr[columnNumber];
		for(int col = 0; col < columnNumber; col++) {
			ColDescr cd = new ColDescr();
			colDescrs[col] = cd;
			// get max column length
			int colIndex = col;
			cd.width = fieldLines.stream().mapToInt(sarr -> sarr[colIndex].length()).max().orElse(0);
			// get alignments
			cd.headerAlign = getTextAlign(col, true);
			cd.columnAlign = getTextAlign(col, false);
		}

		return colDescrs;
	}
	private TextAlignment getTextAlign(int columnIndex, boolean isHeader) {
		TextAlignment toRet = null;

		if(isHeader) {
			toRet = headerFieldAlign.getOrDefault(columnIndex, headerAlign);
		}

		if(toRet == null) {
			toRet = columnFieldAlign.getOrDefault(columnIndex, dataAlign);
		}

		return toRet;
	}

	private class ColDescr {
		int width;
		TextAlignment headerAlign;
		TextAlignment columnAlign;

		String formatText(String source, boolean isHeader) {
			TextAlignment align = isHeader ? headerAlign : columnAlign;
			String toRet;
			if(align == TextAlignment.LEFT) {
				toRet = strf("%-" + width + "s", source);
			} else if(align == TextAlignment.RIGHT) {
				toRet = strf("%" + width + "s", source);
			} else {	// CENTER
				int filler = width - source.length();
				int left = filler / 2;
				String temp = strf("%" + left + "s", source);
				toRet = strf("%-" + width + "s", temp);
			}
			return toRet;
		}
	}

	@Override
	public String toString() {
		return JkStreams.join(lines, StringUtils.LF);
	}
}
