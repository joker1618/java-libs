package xxx.joker.libs.core.format;

/**
 * Created by f.barbano on 26/05/2018.
 */
public enum JkSizeUnit {

	GB("GB", 1024L*1024*1024L),
	MB("MB", 1024L*1024L),
	KB("KB", 1024L),
	B ("B",  1L)
	;


	private String label;
	private long size;

	JkSizeUnit(String label, long size) {
		this.size = size;
		this.label = label;
	}

	public String label() {
		return label;
	}

	public long size() {
		return size;
	}

	public double parse(long numBytes) {
		return parse(numBytes, B);
	}
	public double parse(long amount, JkSizeUnit sizeUnit) {
		return parse((double)amount, sizeUnit);
	}
	public double parse(double numBytes) {
		return parse(numBytes, B);
	}
	public double parse(double amount, JkSizeUnit sizeUnit) {
		return amount * sizeUnit.size() / size;
	}

}
