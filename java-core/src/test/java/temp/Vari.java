package temp;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import xxx.joker.libs.core.datetime.JkDateTime;
import xxx.joker.libs.core.datetime.JkDuration;
import xxx.joker.libs.core.file.JkFiles;
import xxx.joker.libs.core.lambda.JkStreams;
import xxx.joker.libs.core.util.JkStrings;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static xxx.joker.libs.core.util.JkConsole.display;

public class Vari {

    @Test
    public void changeCase() throws Exception {
        Path source = Paths.get("C:\\Users\\fbarbano\\IdeaProjects\\LIBS\\java-libs-branch-refactor-repo\\java-core\\src\\main\\resources\\testData\\nomi.csv");
        Path target = source.getParent().resolve("namesITA.csv");
        List<String> lines = JkFiles.readLines(source);
        List<String> newLines = JkStreams.map(lines, l -> {
            List<String> split = JkStrings.splitList(l, " ");
            return JkStreams.join(split, " ", ll -> StringUtils.capitalize(ll.toLowerCase()));
        });
        JkFiles.writeFile(target, newLines);
    }
    @Test
    public void provacopy() throws Exception {
        Path source = Paths.get("C:\\Users\\fede\\Desktop\\tmp.sh");
        Path target = JkFiles.getParent(source).resolve("Copy_" + source.getFileName().toString());
        JkFiles.copy(source, target);
        display("Source: {}", JkFiles.getLastModifiedTime(source));
        display("Target: {}", JkFiles.getLastModifiedTime(target));
    }
    @Test
    public void provedaas() throws Exception {
        JkDateTime dt = JkFiles.getLastModifiedTime(Paths.get("C:\\Users\\fede\\IdeaProjects\\LIBS\\java-libs\\java-core\\src\\main\\java\\xxx\\joker\\libs\\core\\tests\\JkTests.java"));
        display(dt.toString());
    }
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
        display("%-10s -> %s", elapsed, JkDuration.of(elapsed).strElapsed(false));

        elapsed = "1:21:50";
        display("%-10s -> %s", elapsed, JkDuration.of(elapsed).strElapsed(true));


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


