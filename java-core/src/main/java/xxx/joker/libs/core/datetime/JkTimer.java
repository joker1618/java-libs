package xxx.joker.libs.core.datetime;

import org.apache.commons.lang3.tuple.Pair;
import xxx.joker.libs.core.enumerative.JkSizeUnit;
import xxx.joker.libs.core.format.JkOutput;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static xxx.joker.libs.core.util.JkStrings.strf;

public class JkTimer {

    private long startTm;
    private List<Pair<String, Long>> marks;
    private long endTm;

    public JkTimer() {
        this.startTm = nowMillis();
        this.marks = new ArrayList<>();
        this.endTm = -1L;
    }

    public static JkTimer start() {
        return new JkTimer();
    }

    public void reset() {
        startTm = nowMillis();
        marks.clear();
        endTm = -1L;
    }
    
    public void mark(String label) {
        marks.add(Pair.of(label, nowMillis()));
    }

    public long elapsed() {
        long stop = endTm == -1 ? nowMillis() : endTm;
        return stop - startTm;
    }

    public double elapsed(ChronoUnit chronoUnit) {
        long el = elapsed();
        int denominator;
        switch (chronoUnit) {
            case SECONDS:   denominator = 1000;   break;
            case MINUTES:   denominator = 1000 * 60;   break;
            case HOURS:     denominator = 1000 * 60 * 60;   break;
            default:        denominator = 1;    break;
        }
        return ((double) el) / denominator;
    }

    public void stop() {
        if(endTm == -1) {
            endTm = nowMillis();
        }
    }

    public boolean isStopped() {
        return endTm != -1L;
    }

    public String strElapsed() {
        return JkDuration.strElapsed(elapsed());
    }

    public String strMarks() {
        List<String> lines = new ArrayList<>();
        long begin = startTm;
        for(int i = 0; i < marks.size(); i++) {
            long el = marks.get(i).getValue() - begin;
            lines.add(strf("{}|{}", marks.get(i).getKey(), JkDuration.strElapsed(el)));
        }
        return JkOutput.columnsView(lines);
    }

    public long getStartTm() {
        return startTm;
    }

    public long getEndTm() {
        return endTm;
    }

    public List<Pair<String, Long>> getMarks() {
        return marks;
    }

    private long nowMillis() {
        return System.currentTimeMillis();
    }

}
