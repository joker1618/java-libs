package xxx.joker.libs.excel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import xxx.joker.libs.core.ToAnalyze;

@ToAnalyze
@Deprecated
public class JkExcelChartSerie {
    private String name;
    private List<Double> data;

    public JkExcelChartSerie(String name, int[] data) {
        this.name = name;
        this.data = new ArrayList<>();
        Arrays.stream(data).forEach(d -> this.data.add((double)d));
    }
    public JkExcelChartSerie(String name, double[] data) {
        this.name = name;
        this.data = new ArrayList<>();
        Arrays.stream(data).forEach(d -> this.data.add(d));
    }
    public JkExcelChartSerie(String name, Double[] data) {
        this.name = name;
        this.data = new ArrayList<>();
        this.data.addAll(Arrays.asList(data));
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<Double> getData() {
        return data;
    }
    public void setData(List<Double> data) {
        this.data = data;
    }
}