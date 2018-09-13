package xxx.joker.libs.javalibs.dao.csv;

import org.apache.commons.lang3.StringUtils;

public interface CsvEntity extends Comparable<CsvEntity> {

	String getPrimaryKey();

	@Override
	default int compareTo(CsvEntity other) {
		return StringUtils.compareIgnoreCase(getPrimaryKey(), other.getPrimaryKey());
	}




}
