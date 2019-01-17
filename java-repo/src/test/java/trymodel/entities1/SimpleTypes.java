package trymodel.entities1;

import xxx.joker.libs.repository.design.JkEntityField;
import xxx.joker.libs.repository.design.JkRepoEntity;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class SimpleTypes extends JkRepoEntity {

    @JkEntityField(idx = 0)
    private boolean bool;
    @JkEntityField(idx = 1)
    private Boolean boolWrapper;
    @JkEntityField(idx = 2)
    private int intNum;
    @JkEntityField(idx = 3)
    private Integer intNumWrapper;
    @JkEntityField(idx = 4)
    private long longNum;
    @JkEntityField(idx = 5)
    private Long longNumWrapper;
    @JkEntityField(idx = 6)
    private float floatNum;
    @JkEntityField(idx = 7)
    private Float floatNumWrapper;
    @JkEntityField(idx = 8)
    private double doubeNum;
    @JkEntityField(idx = 9)
    private Double doubeNumWrapper;

    @JkEntityField(idx = 10)
    private LocalTime lt;
    @JkEntityField(idx = 11)
    private LocalDate ld;
    @JkEntityField(idx = 12)
    private LocalDateTime ldt;
    @JkEntityField(idx = 13)
    private File file;
    @JkEntityField(idx = 14)
    private Path path;
    @JkEntityField(idx = 20)
    private String keyword;

    private String unconsidered;

    public SimpleTypes(String keyword) {
        this.keyword = keyword;
    }

    public void setOthers(LocalTime lt, LocalDate ld, LocalDateTime ldt, File file, Path path, String string) {
        this.lt = lt;
        this.ld = ld;
        this.ldt = ldt;
        this.file = file;
        this.path = path;
        this.keyword = string;
    }

    public void setNums(boolean bool, Boolean boolWrapper, int intNum, Integer intNumWrapper, long longNum, Long longNumWrapper, float floatNum, Float floatNumWrapper, double doubeNum, Double doubeNumWrapper) {
        this.bool = bool;
        this.boolWrapper = boolWrapper;
        this.intNum = intNum;
        this.intNumWrapper = intNumWrapper;
        this.longNum = longNum;
        this.longNumWrapper = longNumWrapper;
        this.floatNum = floatNum;
        this.floatNumWrapper = floatNumWrapper;
        this.doubeNum = doubeNum;
        this.doubeNumWrapper = doubeNumWrapper;
    }

    @Override
    public String getPrimaryKey() {
        return keyword;
    }

    public boolean isBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }

    public Boolean getBoolWrapper() {
        return boolWrapper;
    }

    public void setBoolWrapper(Boolean boolWrapper) {
        this.boolWrapper = boolWrapper;
    }

    public int getIntNum() {
        return intNum;
    }

    public void setIntNum(int intNum) {
        this.intNum = intNum;
    }

    public Integer getIntNumWrapper() {
        return intNumWrapper;
    }

    public void setIntNumWrapper(Integer intNumWrapper) {
        this.intNumWrapper = intNumWrapper;
    }

    public long getLongNum() {
        return longNum;
    }

    public void setLongNum(long longNum) {
        this.longNum = longNum;
    }

    public Long getLongNumWrapper() {
        return longNumWrapper;
    }

    public void setLongNumWrapper(Long longNumWrapper) {
        this.longNumWrapper = longNumWrapper;
    }

    public float getFloatNum() {
        return floatNum;
    }

    public void setFloatNum(float floatNum) {
        this.floatNum = floatNum;
    }

    public Float getFloatNumWrapper() {
        return floatNumWrapper;
    }

    public void setFloatNumWrapper(Float floatNumWrapper) {
        this.floatNumWrapper = floatNumWrapper;
    }

    public double getDoubeNum() {
        return doubeNum;
    }

    public void setDoubeNum(double doubeNum) {
        this.doubeNum = doubeNum;
    }

    public Double getDoubeNumWrapper() {
        return doubeNumWrapper;
    }

    public void setDoubeNumWrapper(Double doubeNumWrapper) {
        this.doubeNumWrapper = doubeNumWrapper;
    }

    public LocalTime getLt() {
        return lt;
    }

    public void setLt(LocalTime lt) {
        this.lt = lt;
    }

    public LocalDate getLd() {
        return ld;
    }

    public void setLd(LocalDate ld) {
        this.ld = ld;
    }

    public LocalDateTime getLdt() {
        return ldt;
    }

    public void setLdt(LocalDateTime ldt) {
        this.ldt = ldt;
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

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getUnconsidered() {
        return unconsidered;
    }

    public void setUnconsidered(String unconsidered) {
        this.unconsidered = unconsidered;
    }
}
