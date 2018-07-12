package xxx.joker.libs.javalibs.dao.csv;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static xxx.joker.libs.javalibs.utils.JkConsole.display;
import static xxx.joker.libs.javalibs.utils.JkStrings.strf;

public class TestJkCsvDao {
	
	@Test
	public void various() throws Exception {
		display("A hash 292938459");
		display("C hash %d", C.class.hashCode());
		display("B hash 917142466");
		display("B hash %d", B.class.hashCode());

		display("end");
	}
	class C {}
	class B {}

	@Test
	public void testDao() throws Exception {
		// Create list
		List<CsvObj> list = new ArrayList<>();
		list.add(new CsvObj());
		list.add(getFilledCsvObj(1));
		list.add(getFilledCsvObj(2));

		// Persist
		Path dbPath = Paths.get("src/test/resources/csvDao/testFile.db");
		JkCsvDaoNew<CsvObj> csvDao = new JkCsvDaoNew<>(dbPath, CsvObj.class);
//		csvDao.persist(list);
//		display("Persisted DB second file %s", dbPath);

		// Read
		List<CsvObj> readList = csvDao.readAll();
		int diffNum = 0;
		for(int i = 0; i < list.size(); i++) {
			if(!list.get(i).equals(readList.get(i))) {
				display("Elem %d differs", i);
				diffNum++;
			}
		}

		Assert.assertEquals(0, diffNum);
		display("All elements are equals");
	}


	private CsvObj getFilledCsvObj(int mult) {
		CsvObj co = new CsvObj();
//		co.setaBoolean(true);
//		co.setwBoolean(false);
//		co.setaInt(10 * mult);
//		co.setwInt(12 * mult);
//		co.setaLong(156L * mult);
//		co.setwLong(-569L * mult);
//		co.setaFloat(15.587f * mult);
//		co.setwFloat(-890.45f * mult);
//		co.setaDouble(1569.56 * mult);
//		co.setwDouble(654.235 * mult);
//		co.setFile(new File("pippo.txt"));
//		co.setPath(Paths.get("pluto.txt"));
//		co.setLtime(LocalTime.of(11, (34*mult)%60, 44));
//		co.setLdate(LocalDate.of(2018, ((5*mult)%12)+1, 13));
//		co.setLdt(LocalDateTime.of(2018, ((7*mult)%12)+1, 23, 20, 31, 26));
//		co.setString("federico");
		co.setListInt(Arrays.asList(12, 3*mult, 67));
		co.setListLDate(Arrays.asList(LocalDate.now(), LocalDate.now().plusDays(4)));
		co.setSub1(new SubCsv1("fede", Arrays.asList(new SubCsv2("asdd"), new SubCsv2("QQEE")), new SubCsv2("maronna")));
		co.setSub1List(Arrays.asList(
			new SubCsv1("zio", Collections.emptyList(), new SubCsv2("dioffa")),
			new SubCsv1("mamma", Collections.emptyList(), new SubCsv2("grooossso")),
			new SubCsv1("pao", Arrays.asList(new SubCsv2("ZXD"), new SubCsv2("d fg g")), new SubCsv2("fineale")))
		);
		return co;
	}
}

class SubCsv1 implements CsvElement {

	private String elemID;

	@CsvField(index = 0, header = "str")
	private String str="";
	@CsvField(index = 1, header = "refs", listElemType = SubCsv2.class)
	private List<SubCsv2> refs;
	@CsvField(index = 2, header = "ref_obj")
	private SubCsv2 refObj;

	public SubCsv1() {
	}

	public SubCsv1(String str, List<SubCsv2> refs, SubCsv2 refObj) {
		this.str = str;
		this.refs = refs;
		this.refObj = refObj;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public List<SubCsv2> getRefs() {
		return refs;
	}

	public void setRefs(List<SubCsv2> refs) {
		this.refs = refs;
	}

	public SubCsv2 getRefObj() {
		return refObj;
	}

	public void setRefObj(SubCsv2 refObj) {
		this.refObj = refObj;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SubCsv1)) return false;

		SubCsv1 subCsv1 = (SubCsv1) o;

		if (str != null ? !str.equals(subCsv1.str) : subCsv1.str != null) return false;
		if (refs != null ? !refs.equals(subCsv1.refs) : subCsv1.refs != null) return false;
		return refObj != null ? refObj.equals(subCsv1.refObj) : subCsv1.refObj == null;
	}

	@Override
	public int hashCode() {
		int result = str != null ? str.hashCode() : 0;
		result = 31 * result + (refs != null ? refs.hashCode() : 0);
		result = 31 * result + (refObj != null ? refObj.hashCode() : 0);
		return result;
	}

	@Override
	public String getClassHash() {
		return "SD46843959";
	}

	@Override
	public String getElemID() {
		if(elemID == null)	elemID = String.valueOf(hashCode());
		return elemID;
	}

	@Override
	public void setElemID(String elemID) {
		this.elemID = elemID;
	}


}

class SubCsv2 implements CsvElement {

	private String elemID;

	@CsvField(index = 0, header = "str")
	private String str = "";

	public SubCsv2() {
	}

	public SubCsv2(String str) {
		this.str = str;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}


	@Override
	public String getClassHash() {
		return "46843xxdf959";
	}

	@Override
	public String getElemID() {
		if(elemID == null)	elemID = String.valueOf(hashCode());
		return elemID;
	}

	@Override
	public void setElemID(String elemID) {
		this.elemID = elemID;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SubCsv2)) return false;

		SubCsv2 subCsv2 = (SubCsv2) o;

		return str != null ? str.equals(subCsv2.str) : subCsv2.str == null;
	}

	@Override
	public int hashCode() {
		return str != null ? str.hashCode() : 0;
	}
}

class CsvObj implements CsvElement {

	private String elemID;

//	@CsvField(index = 0, header = "bool")
//	private boolean aBoolean;
//	@CsvField(index = 1, header = "W_bool")
//	private Boolean wBoolean;
//	@CsvField(index = 2, header = "int")
//	private int aInt;
//	@CsvField(index = 3, header = "W_int")
//	private Integer wInt;
//	@CsvField(index = 4, header = "long")
//	private long aLong;
//	@CsvField(index = 5, header = "W_long")
//	private Long wLong;
//	@CsvField(index = 6, header = "float")
//	private float aFloat;
//	@CsvField(index = 7, header = "W_float")
//	private Float wFloat;
//	@CsvField(index = 8, header = "double")
//	private double aDouble;
//	@CsvField(index = 9, header = "W_double")
//	private Double wDouble;
//	@CsvField(index = 10, header = "File")
//	private File file;
//	@CsvField(index = 11, header = "Path")
//	private Path path;
//	@CsvField(index = 12, header = "LocalTime")
//	private LocalTime ltime;
//	@CsvField(index = 13, header = "LocalDate")
//	private LocalDate ldate;
//	@CsvField(index = 14, header = "LocalDateTime")
//	private LocalDateTime ldt;
//	@CsvField(index = 15, header = "String")
//	private String string = "";
	@CsvField(index = 18, header = "List_int", listElemType = Integer.class)
	private List<Integer> listInt = new ArrayList<>();
	@CsvField(index = 19, header = "List_LocalDate", listElemType = LocalDate.class)
	private List<LocalDate> listLDate = new ArrayList<>();
	@CsvField(index = 20, header = "SUB 1")
	private SubCsv1 sub1;
	@CsvField(index = 21, header = "SUB 1 LIST", listElemType = SubCsv1.class)
	private List<SubCsv1> sub1List = new ArrayList<>();

	public SubCsv1 getSub1() {
		return sub1;
	}

	public void setSub1(SubCsv1 sub1) {
		this.sub1 = sub1;
	}

	public List<SubCsv1> getSub1List() {
		return sub1List;
	}

	public void setSub1List(List<SubCsv1> sub1List) {
		this.sub1List = sub1List;
	}

	public List<Integer> getListInt() {
		return listInt;
	}

	public void setListInt(List<Integer> listInt) {
		this.listInt = listInt;
	}

	public List<LocalDate> getListLDate() {
		return listLDate;
	}

	public void setListLDate(List<LocalDate> listLDate) {
		this.listLDate = listLDate;
	}

//	public boolean isaBoolean() {
//		return aBoolean;
//	}
//	public void setaBoolean(boolean aBoolean) {
//		this.aBoolean = aBoolean;
//	}
//	public Boolean getwBoolean() {
//		return wBoolean;
//	}
//	public void setwBoolean(Boolean wBoolean) {
//		this.wBoolean = wBoolean;
//	}
//	public int getaInt() {
//		return aInt;
//	}
//	public void setaInt(int aInt) {
//		this.aInt = aInt;
//	}
//	public Integer getwInt() {
//		return wInt;
//	}
//	public void setwInt(Integer wInt) {
//		this.wInt = wInt;
//	}
//	public long getaLong() {
//		return aLong;
//	}
//	public void setaLong(long aLong) {
//		this.aLong = aLong;
//	}
//	public Long getwLong() {
//		return wLong;
//	}
//	public void setwLong(Long wLong) {
//		this.wLong = wLong;
//	}
//	public float getaFloat() {
//		return aFloat;
//	}
//	public void setaFloat(float aFloat) {
//		this.aFloat = aFloat;
//	}
//	public Float getwFloat() {
//		return wFloat;
//	}
//	public void setwFloat(Float wFloat) {
//		this.wFloat = wFloat;
//	}
//	public double getaDouble() {
//		return aDouble;
//	}
//	public void setaDouble(double aDouble) {
//		this.aDouble = aDouble;
//	}
//	public Double getwDouble() {
//		return wDouble;
//	}
//	public void setwDouble(Double wDouble) {
//		this.wDouble = wDouble;
//	}
//	public File getFile() {
//		return file;
//	}
//	public void setFile(File file) {
//		this.file = file;
//	}
//	public Path getPath() {
//		return path;
//	}
//	public void setPath(Path path) {
//		this.path = path;
//	}
//	public LocalTime getLtime() {
//		return ltime;
//	}
//	public void setLtime(LocalTime ltime) {
//		this.ltime = ltime;
//	}
//	public LocalDate getLdate() {
//		return ldate;
//	}
//	public void setLdate(LocalDate ldate) {
//		this.ldate = ldate;
//	}
//	public LocalDateTime getLdt() {
//		return ldt;
//	}
//	public void setLdt(LocalDateTime ldt) {
//		this.ldt = ldt;
//	}
//	public String getString() {
//		return string;
//	}
//	public void setString(String string) {
//		this.string = string;
//	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CsvObj)) return false;

		CsvObj csvObj = (CsvObj) o;

//		if (aBoolean != csvObj.aBoolean) return false;
//		if (aInt != csvObj.aInt) return false;
//		if (aLong != csvObj.aLong) return false;
//		if (Float.compare(csvObj.aFloat, aFloat) != 0) return false;
//		if (Double.compare(csvObj.aDouble, aDouble) != 0) return false;
//		if (wBoolean != null ? !wBoolean.equals(csvObj.wBoolean) : csvObj.wBoolean != null) return false;
//		if (wInt != null ? !wInt.equals(csvObj.wInt) : csvObj.wInt != null) return false;
//		if (wLong != null ? !wLong.equals(csvObj.wLong) : csvObj.wLong != null) return false;
//		if (wFloat != null ? !wFloat.equals(csvObj.wFloat) : csvObj.wFloat != null) return false;
//		if (wDouble != null ? !wDouble.equals(csvObj.wDouble) : csvObj.wDouble != null) return false;
//		if (file != null ? !file.equals(csvObj.file) : csvObj.file != null) return false;
//		if (path != null ? !path.equals(csvObj.path) : csvObj.path != null) return false;
//		if (ltime != null ? !ltime.equals(csvObj.ltime) : csvObj.ltime != null) return false;
//		if (ldate != null ? !ldate.equals(csvObj.ldate) : csvObj.ldate != null) return false;
//		if (ldt != null ? !ldt.equals(csvObj.ldt) : csvObj.ldt != null) return false;
//		if (string != null ? !string.equals(csvObj.string) : csvObj.string != null) return false;
		if (listInt != null ? !listInt.equals(csvObj.listInt) : csvObj.listInt != null) return false;
		if (listLDate != null ? !listLDate.equals(csvObj.listLDate) : csvObj.listLDate != null) return false;
		if (sub1 != null ? !sub1.equals(csvObj.sub1) : csvObj.sub1 != null) return false;
		return sub1List != null ? sub1List.equals(csvObj.sub1List) : csvObj.sub1List == null;
	}

	@Override
	public int hashCode() {
		int result = 0;
		long temp;
//		result = (aBoolean ? 1 : 0);
//		result = 31 * result + (wBoolean != null ? wBoolean.hashCode() : 0);
//		result = 31 * result + aInt;
//		result = 31 * result + (wInt != null ? wInt.hashCode() : 0);
//		result = 31 * result + (int) (aLong ^ (aLong >>> 32));
//		result = 31 * result + (wLong != null ? wLong.hashCode() : 0);
//		result = 31 * result + (aFloat != +0.0f ? Float.floatToIntBits(aFloat) : 0);
//		result = 31 * result + (wFloat != null ? wFloat.hashCode() : 0);
//		temp = Double.doubleToLongBits(aDouble);
//		result = 31 * result + (int) (temp ^ (temp >>> 32));
//		result = 31 * result + (wDouble != null ? wDouble.hashCode() : 0);
//		result = 31 * result + (file != null ? file.hashCode() : 0);
//		result = 31 * result + (path != null ? path.hashCode() : 0);
//		result = 31 * result + (ltime != null ? ltime.hashCode() : 0);
//		result = 31 * result + (ldate != null ? ldate.hashCode() : 0);
//		result = 31 * result + (ldt != null ? ldt.hashCode() : 0);
//		result = 31 * result + (string != null ? string.hashCode() : 0);
		result = 31 * result + (listInt != null ? listInt.hashCode() : 0);
		result = 31 * result + (listLDate != null ? listLDate.hashCode() : 0);
		result = 31 * result + (sub1 != null ? sub1.hashCode() : 0);
		result = 31 * result + (sub1List != null ? sub1List.hashCode() : 0);
		return result;
	}

	@Override
	public String getClassHash() {
		return "123456";
	}

	@Override
	public String getElemID() {
		if(elemID == null) {
			elemID = strf("%d", hashCode());
		}
		return elemID;
	}

	@Override
	public void setElemID(String elemID) {
		this.elemID = elemID;
	}
}
