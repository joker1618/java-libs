package xxx.joker.libs.core.debug;

import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.core.datetime.JkTimer;
import xxx.joker.libs.core.format.JkOutput;
import xxx.joker.libs.core.lambdas.JkStreams;
import xxx.joker.libs.core.runtimes.JkRuntime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkStrings.strf;

public class JkDebug {

    private static final AtomicLong idSeq = new AtomicLong(0L);
    private static final TreeMap<Long, DTimer> opened = new TreeMap<>();
    private static final List<DTimer> closed = new ArrayList<>();


    public static long startTimerNum(int stepNum) {
        return startTimer("STEP {}", stepNum);
    }
    public static long startTimer(String label, Object... params) {
        synchronized (opened) {
            long id = idSeq.getAndIncrement();
            opened.put(id, new DTimer(id, strf(label, params)));
            return id;
        }
    }

    public static void stopTimerNum(int stepNum) {
        stopTimer("STEP {}", stepNum);
    }
    public static void stopTimer(long id) {
        synchronized (opened) {
            DTimer dt = opened.remove(id);
            dt.getTimer().stop();
            closed.add(dt);
        }
    }
    /**
     * Close the last timer with the same label
     */
    public static void stopTimer(String label, Object... params) {
        synchronized (opened) {
            List<Long> decrIDs = JkStreams.reverseOrder(opened.keySet());
            String lbl = strf(label, params);
            for(Long id : decrIDs) {
                if(opened.get(id).getLabel().equals(lbl)) {
                    stopTimer(id);
                    return;
                }
            }
        }
    }

    public static void displayTimes() {
        displayTimes(true);
    }
    public static void displayTimes(boolean showTotJvmTime) {
        synchronized (opened) {
            Long totMilli = 0L;
            if(showTotJvmTime) {
                totMilli = System.currentTimeMillis() - JkRuntime.getJvmStartTime();
            }

            List<String> orderedLabels = JkStreams.distinct(JkStreams.map(closed, DTimer::getLabel));
            Map<String, List<DTimer>> map = JkStreams.toMap(closed, DTimer::getLabel);

            boolean multi = false;
            List<String> lines = new ArrayList<>();
            for (String lbl : orderedLabels) {
                List<DTimer> dtList = map.get(lbl);
                long sum = JkStreams.sumLong(dtList, dt -> dt.getTimer().elapsed());
                JkDuration durTot = JkDuration.of(sum);

                String str = strf("{}|{}", lbl, durTot.toStringElapsed());
                if(totMilli > 0L) {
                    int perc = (int)(sum * 100d / totMilli);
                    str += strf("|%3s%%", perc);
                }

                if(dtList.size() > 1) {
                    multi = true;
                    double each = (double) sum / dtList.size();
                    JkDuration durEach = JkDuration.of(each);
                    str += strf("|{}|{}", dtList.size(), durEach.toStringElapsed());
                    if(totMilli > 0L) {
                        double perc2 = 100d * each / totMilli;
                        str += strf("|%6s%%", strf("%.2f", perc2));
                    }
                }

                lines.add(str);
            }

            if(showTotJvmTime) {
                lines.add(strf("TOTAL|{}", JkDuration.toStringElapsed(totMilli)));
            }

            String header = "LABEL|TIME";
            if(totMilli > 0L)   header += "|%";
            if(multi) {
                header += "|NUM|EACH";
                if(totMilli > 0L)   header += "|%";
            }

            lines.add(0, header);

            display("###  DEBUG TIMES  ###");
            display(JkOutput.columnsView(lines, true));
        }


    }

    private static class DTimer {
        private long id;
        private String label;
        private JkTimer timer;

        public DTimer(long id, String label) {
            this.id = id;
            this.label = label;
            this.timer = new JkTimer();
        }

        public long getId() {
            return id;
        }

        public String getLabel() {
            return label;
        }

        public JkTimer getTimer() {
            return timer;
        }
    }
}
