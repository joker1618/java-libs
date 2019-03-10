package xxx.joker.libs.core.datetime;

import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;

public class JkTimer {

    private long startTm;
    private List<Long> marksTm;
    private long endTm;

    public JkTimer() {
        this.startTm = nowMillis();
        this.marksTm = new ArrayList<>();
    }
    
    public void reset() {
        startTm = nowMillis();
        marksTm.clear();
    }
    
    public long mark() {
        long now = nowMillis();
        long from = marksTm.isEmpty() ? startTm : marksTm.get(marksTm.size()-1);
        this.marksTm.add(now);
        return now - from;
    }

    public long elapsed() {
        long stop = marksTm.isEmpty() ? nowMillis() : marksTm.get(marksTm.size()-1);
        return stop - startTm;
    }
    public long totalElapsed() {
        return nowMillis() - startTm;
    }

    
    private long nowMillis() {
        return System.currentTimeMillis();
    }

}
