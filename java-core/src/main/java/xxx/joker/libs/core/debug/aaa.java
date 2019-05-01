package xxx.joker.libs.core.debug;

import java.lang.management.ManagementFactory;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class aaa {

    public static void main(String[] args) throws InterruptedException {
        long st = System.currentTimeMillis();
        Thread.sleep(500);
        String str = "fede";
        display(str);
        long id1 = JkDebug.startTimer("Timer 1");
        Thread.sleep(30);
        long id2 = JkDebug.startTimer("Timer 2");
        long id3 = JkDebug.startTimer("Timer 2");
        Thread.sleep(42);
        JkDebug.stopTimer(id1);
        Thread.sleep(34);
        JkDebug.stopTimer("Timer 2");
        Thread.sleep(66);
        JkDebug.stopTimer(id2);
        JkDebug.displayTimes(false);
        display("");
        JkDebug.displayTimes(true);
        display("END");


    }
}
