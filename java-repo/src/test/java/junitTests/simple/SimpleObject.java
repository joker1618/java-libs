package junitTests.simple;

import xxx.joker.libs.core.enumerative.JkAlign;
import xxx.joker.libs.core.test.JkDataTest;
import xxx.joker.libs.repo.design.SimpleRepoEntity;
import xxx.joker.libs.repo.design.annotation.marker.EntityField;
import xxx.joker.libs.repo.design.annotation.marker.EntityPK;

import java.time.LocalDateTime;
import java.util.*;

public class SimpleObject extends SimpleRepoEntity {

    @EntityPK
    private Double doubleWrapper;
    @EntityField
    private boolean bool;
    @EntityField
    private LocalDateTime ldt;
    @EntityField
    private List<Integer> intList;
    @EntityField
    private Set<String> stringSet;
    @EntityField
    private Map<JkAlign, Integer> alignMap;
    @EntityField
    private Map<Integer, List<String>> mapListStrings;
    @EntityField
    private Set<List<List<String>>> setList;
    @EntityField
    private Map<List<List<Integer>>, List<String>> superMap;

    public SimpleObject() {

    }
    public SimpleObject(JkDataTest dataTest) {
        bool = dataTest.nextBoolean();
        doubleWrapper = dataTest.nextDouble();
//        ldt = dataTest.nextLdt();
        intList = dataTest.nextInts(3, 100);
        stringSet = new TreeSet<>(dataTest.nextCountries(3));
        alignMap = new LinkedHashMap<>();
        for (JkAlign align : JkAlign.values()) {
            alignMap.put(align, dataTest.nextInt(100));
        }
        mapListStrings = new TreeMap<>();
        mapListStrings.put(dataTest.nextInt(), dataTest.nextCountries(3));
        mapListStrings.put(dataTest.nextInt(), dataTest.nextCountries(3));

        setList = new LinkedHashSet<>();
        setList.add(Arrays.asList(dataTest.nextNames(2), dataTest.nextNames(2)));
        setList.add(Arrays.asList(dataTest.nextNames(2), dataTest.nextNames(2)));

        superMap = new LinkedHashMap<>();
        superMap.put(Arrays.asList(dataTest.nextInts(2), dataTest.nextInts(2)), dataTest.nextCountries(3));
        superMap.put(Arrays.asList(dataTest.nextInts(2), dataTest.nextInts(2)), dataTest.nextCountries(3));
    }

    public void setToNullForTest() {
        doubleWrapper = null;
        ldt = null;
        intList.add(null);
        alignMap.clear();
        for (JkAlign align : JkAlign.values()) {
            alignMap.put(align, null);
        }
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

}
