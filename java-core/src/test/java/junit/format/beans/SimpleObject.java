package junit.format.beans;

import org.apache.commons.lang3.tuple.Pair;
import xxx.joker.libs.core.enumerative.JkAlign;
import xxx.joker.libs.core.test.JkDataTest;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class SimpleObject {

    private boolean bool;
    private Double doubleWrapper;
    private LocalDateTime ldt;
    private List<Integer> intList;
    private Set<String> stringSet;
    private Map<JkAlign, Integer> alignMap;
    private Pair<Integer, String> pairSimple;
    private Map<Integer, List<String>> mapListStrings;
    private Set<List<List<String>>> setList;

    public SimpleObject() {

    }
    public SimpleObject(JkDataTest dataTest) {
        bool = dataTest.nextBoolean();
        doubleWrapper = dataTest.nextDouble();
        ldt = LocalDateTime.now();
        intList = dataTest.nextInts(3, 100);
        stringSet = new TreeSet<>(dataTest.nextCountries(3));
        alignMap = new LinkedHashMap<>();
        for (JkAlign align : JkAlign.values()) {
            alignMap.put(align, dataTest.nextInt(100));
        }
        pairSimple = Pair.of(dataTest.nextInt(100), dataTest.nextName());
        mapListStrings = new TreeMap<>();
        mapListStrings.put(dataTest.nextInt(), dataTest.nextCountries(3));
        mapListStrings.put(dataTest.nextInt(), dataTest.nextCountries(3));
        setList = new LinkedHashSet<>();
        List<List<String>> ll1 = Arrays.asList(dataTest.nextNames(2), dataTest.nextNames(2));
        List<List<String>> ll2 = Arrays.asList(dataTest.nextNames(2), dataTest.nextNames(2));
        setList.add(ll1);
        setList.add(ll2);
    }

    public void setToNullForTest() {
        doubleWrapper = null;
        ldt = null;
        intList.add(null);
        alignMap.clear();
        for (JkAlign align : JkAlign.values()) {
            alignMap.put(align, null);
        }
        pairSimple = Pair.of(null, null);
    }

    public boolean isBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }

    public Double getDoubleWrapper() {
        return doubleWrapper;
    }

    public void setDoubleWrapper(Double doubleWrapper) {
        this.doubleWrapper = doubleWrapper;
    }

    public LocalDateTime getLdt() {
        return ldt;
    }

    public void setLdt(LocalDateTime ldt) {
        this.ldt = ldt;
    }

    public List<Integer> getIntList() {
        return intList;
    }

    public void setIntList(List<Integer> intList) {
        this.intList = intList;
    }

    public Set<String> getStringSet() {
        return stringSet;
    }

    public void setStringSet(Set<String> stringSet) {
        this.stringSet = stringSet;
    }

    public Map<JkAlign, Integer> getAlignMap() {
        return alignMap;
    }

    public void setAlignMap(Map<JkAlign, Integer> alignMap) {
        this.alignMap = alignMap;
    }

    public Pair<Integer, String> getPairSimple() {
        return pairSimple;
    }

    public void setPairSimple(Pair<Integer, String> pairSimple) {
        this.pairSimple = pairSimple;
    }
}
