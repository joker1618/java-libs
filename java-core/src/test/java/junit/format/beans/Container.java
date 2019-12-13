package junit.format.beans;

import javafx.util.Pair;
import xxx.joker.libs.core.enumerative.JkAlign;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Container {

    private boolean bp;
    private int ip;
    private long lp;
    private float fp;
    private double dp;
    
    private Boolean bw;
    private Integer iw;
    private Long lw;
    private Float fw;
    private Double dw;

    private LocalTime ltm;
    private LocalDate ldt;
    private LocalDateTime ldtm;

    private File file;
    private Path path;

    private Class<?> clazz;
    private JkAlign enumAlign;
    private Pair<Integer, Double> pairSimple;
}
