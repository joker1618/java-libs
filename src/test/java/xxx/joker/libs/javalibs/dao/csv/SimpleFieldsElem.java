package xxx.joker.libs.javalibs.dao.csv;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class SimpleFieldsElem implements CsvTable {

    @Override
    public String getPrimaryKey() {
        return null;
    }
}
/*
public class SimpleFieldsElem implements CsvTable {

    @CsvField(index = 0, header = "bool")
    private boolean aBoolean;
    @CsvField(index = 1, header = "W_bool")
    private Boolean wBoolean;
    @CsvField(index = 2, header = "int")
    private int aInt;
    @CsvField(index = 3, header = "W_int")
    private Integer wInt;
    @CsvField(index = 4, header = "long")
    private long aLong;
    @CsvField(index = 5, header = "W_long")
    private Long wLong;
    @CsvField(index = 6, header = "float")
    private float aFloat;
    @CsvField(index = 7, header = "W_float")
    private Float wFloat;
    @CsvField(index = 8, header = "double")
    private double aDouble;
    @CsvField(index = 9, header = "W_double")
    private Double wDouble;
    @CsvField(index = 10, header = "File")
    private File file;
    @CsvField(index = 11, header = "Path")
    private Path path;
    @CsvField(index = 12, header = "LocalTime")
    private LocalTime ltime;
    @CsvField(index = 13, header = "LocalDate")
    private LocalDate ldate;
    @CsvField(index = 14, header = "LocalDateTime")
    private LocalDateTime ldt;
    @CsvField(index = 15, header = "String")
    private String string = "";


    public boolean isaBoolean() {
        return aBoolean;
    }
    public void setaBoolean(boolean aBoolean) {
        this.aBoolean = aBoolean;
    }
    public Boolean getwBoolean() {
        return wBoolean;
    }
    public void setwBoolean(Boolean wBoolean) {
        this.wBoolean = wBoolean;
    }
    public int getaInt() {
        return aInt;
    }
    public void setaInt(int aInt) {
        this.aInt = aInt;
    }
    public Integer getwInt() {
        return wInt;
    }
    public void setwInt(Integer wInt) {
        this.wInt = wInt;
    }
    public long getaLong() {
        return aLong;
    }
    public void setaLong(long aLong) {
        this.aLong = aLong;
    }
    public Long getwLong() {
        return wLong;
    }
    public void setwLong(Long wLong) {
        this.wLong = wLong;
    }
    public float getaFloat() {
        return aFloat;
    }
    public void setaFloat(float aFloat) {
        this.aFloat = aFloat;
    }
    public Float getwFloat() {
        return wFloat;
    }
    public void setwFloat(Float wFloat) {
        this.wFloat = wFloat;
    }
    public double getaDouble() {
        return aDouble;
    }
    public void setaDouble(double aDouble) {
        this.aDouble = aDouble;
    }
    public Double getwDouble() {
        return wDouble;
    }
    public void setwDouble(Double wDouble) {
        this.wDouble = wDouble;
    }
    public File getFile() {
        return file;
    }
    public void setFile(File file) {
        this.file = file;
    }
    public Path getPath() {
        return path;
    }
    public void setPath(Path path) {
        this.path = path;
    }
    public LocalTime getLtime() {
        return ltime;
    }
    public void setLtime(LocalTime ltime) {
        this.ltime = ltime;
    }
    public LocalDate getLdate() {
        return ldate;
    }
    public void setLdate(LocalDate ldate) {
        this.ldate = ldate;
    }
    public LocalDateTime getLdt() {
        return ldt;
    }
    public void setLdt(LocalDateTime ldt) {
        this.ldt = ldt;
    }
    public String getString() {
        return string;
    }
    public void setString(String string) {
        this.string = string;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleFieldsElem that = (SimpleFieldsElem) o;
        return isaBoolean() == that.isaBoolean() &&
                getaInt() == that.getaInt() &&
                getaLong() == that.getaLong() &&
                Float.compare(that.getaFloat(), getaFloat()) == 0 &&
                Double.compare(that.getaDouble(), getaDouble()) == 0 &&
                Objects.equals(getwBoolean(), that.getwBoolean()) &&
                Objects.equals(getwInt(), that.getwInt()) &&
                Objects.equals(getwLong(), that.getwLong()) &&
                Objects.equals(getwFloat(), that.getwFloat()) &&
                Objects.equals(getwDouble(), that.getwDouble()) &&
                Objects.equals(getFile(), that.getFile()) &&
                Objects.equals(getPath(), that.getPath()) &&
                Objects.equals(getLtime(), that.getLtime()) &&
                Objects.equals(getLdate(), that.getLdate()) &&
                Objects.equals(getLdt(), that.getLdt()) &&
                Objects.equals(getString(), that.getString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(isaBoolean(), getwBoolean(), getaInt(), getwInt(), getaLong(), getwLong(), getaFloat(), getwFloat(), getaDouble(), getwDouble(), getFile(), getPath(), getLtime(), getLdate(), getLdt(), getString());
    }

    @Override
    public String getPrimaryKey() {
        return hashCode()+"";
    }

}
*/