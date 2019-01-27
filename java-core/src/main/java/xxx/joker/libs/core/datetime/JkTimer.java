package xxx.joker.libs.core.datetime;

import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;

// todo impl
public class JkTimer {

    private long startTm;
    private List<Long> marksTm;
    private long endTm;

    public JkTimer() {
        this(false);
    }
    public JkTimer(boolean autoStart) {
        this.startTm = autoStart ? nowMillis() : -1;
        this.marksTm = new ArrayList<>();
        this.endTm = -1L;
    }
    
    public void start() {
        if(startTm == -1L) {
            startTm = nowMillis();
        }
    }
    
    public void reset() {
        startTm = nowMillis();
        marksTm.clear();
        endTm = -1L;
    }
    
    public void mark() {
        this.marksTm.add(nowMillis());
    }

    public void stop() {
        this.endTm = nowMillis();
    }

    public long elapsed() {
        return elapsed(false);
    }
    public long elapsed(boolean stopTimer) {
        if(startTm == -1)   return -1L;
        if(stopTimer)   endTm = nowMillis();
        long stop = endTm == -1 ? nowMillis() : endTm;
        return stop - startTm;
    }

    
    private long nowMillis() {
        return System.currentTimeMillis();
    }

}
