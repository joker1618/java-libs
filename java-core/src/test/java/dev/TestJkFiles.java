package dev;

import org.junit.Test;
import xxx.joker.libs.core.datetime.JkTimer;
import xxx.joker.libs.core.datetime.JkDates;
import xxx.joker.libs.core.files.JkFiles;

import java.nio.file.Path;
import java.nio.file.Paths;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class TestJkFiles {

    @Test
    public void testFind() {
        Path root = Paths.get("C:\\Users\\f.barbano\\Desktop\\moveTest\\sec\\1 Studio albums\\1974h");
        JkFiles.findFiles(root, false).forEach(p -> display("  {}", p));
    }

    @Test
    public void testRemoveFile() {
        Path root = Paths.get("C:\\Users\\f.barbano\\Desktop\\moveTest\\remove\\back.jpg");
//        Path root = Paths.get("C:\\Users\\f.barbano\\Desktop\\BubbleChart.jpeg");
        JkFiles.remove(root);
    }

    @Test
    public void testRemoveFolder() {
        Path root = Paths.get("C:\\Users\\f.barbano\\Desktop\\moveTest\\remove");
//        Path root = Paths.get("C:\\Users\\f.barbano\\Desktop\\BubbleChart.jpeg");
        JkFiles.remove(root);
    }

    @Test
    public void moveSingleFile() {
        Path inputPath = Paths.get("C:\\Users\\f.barbano\\Desktop\\moveTest\\sec\\back.jpg");
        Path outputPath = Paths.get("C:\\Users\\f.barbano\\Desktop\\moveTest\\as\\fedeBack.jpg");
        JkFiles.moveFile(inputPath, outputPath, true);
    }

    @Test
    public void copySingleFile() {
        Path inputPath = Paths.get("C:\\Users\\f.barbano\\Desktop\\moveTest\\sec\\back.jpg");
        Path outputPath = Paths.get("C:\\Users\\f.barbano\\Desktop\\moveTest\\de\\fedeBackCopy.jpg");
        JkFiles.copyFile(inputPath, outputPath, true);
    }

    @Test
    public void copySingleFile2() {
        Path inputPath = Paths.get("C:\\Users\\f.barbano\\Desktop\\from");
        Path outputPath = Paths.get("C:\\Users\\f.barbano\\Desktop\\to");
        JkTimer timer = new JkTimer(true);
        JkFiles.copyFile(inputPath, outputPath, true);
        display("Copy completed in {}", JkDates.toStringElapsed(timer.elapsed(), true));
    }

    @Test
    public void moveFolder() {
        Path inputPath = Paths.get("C:\\Users\\f.barbano\\Desktop\\moveTest\\sec");
        Path outputPath = Paths.get("C:\\Users\\f.barbano\\Desktop\\moveTest\\dio");
        JkFiles.moveFile(inputPath, outputPath, true);
    }

    @Test
    public void copyFolder() {
        Path inputPath = Paths.get("C:\\Users\\f.barbano\\Desktop\\moveTest\\sec");
        Path outputPath = Paths.get("C:\\Users\\f.barbano\\Desktop\\moveTest\\fe\\dio");
        JkFiles.copyFile(inputPath, outputPath, true);
    }




}