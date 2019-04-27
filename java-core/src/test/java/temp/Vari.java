package temp;

import org.junit.Test;
import xxx.joker.libs.core.datetime.JkDuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class Vari {

    @Test
    public void provaas() throws Exception {
        SyncClazz sc = new SyncClazz();
        List<Thread> threadList = new ArrayList<>();
        Random random = new Random(System.currentTimeMillis());
        threadList.add(new Thread(() -> {
            for(int i = 0; i < 10; i++) {
//                random.nextBoolean()
                display("AAA  callGet {}", sc.callGet());
            }
            display("AAA end");
        }));
        threadList.add(new Thread(() -> {
            for(int i = 0; i < 10; i++) {
//                random.nextBoolean()
                display("ZZZ  callGet {}", sc.callGet());
            }
            display("ZZZ end");
        }));

        threadList.forEach(Thread::start);

        for (Thread thread : threadList) {
            thread.join();
        }

        display("END MAIN");
    }

    @Test
    public void prova() throws IOException, ParseException {

        String elapsed = "1:0.234";
        display("%-10s -> %s", elapsed, JkDuration.of(elapsed).toStringElapsed(false));

        elapsed = "1:21:50";
        display("%-10s -> %s", elapsed, JkDuration.of(elapsed).toStringElapsed(true));


    }

    private class SyncClazz {

        private int num = 10;

        public synchronized int callGet() {
            return get();
        }

        public synchronized int get() {
            return num;
        }
    }
}


