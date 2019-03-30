package trymodel;

import org.junit.Test;
import trymodel.entities1.SimpleTypes;
import xxx.joker.libs.repository.JkRepoFile;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class Test1 extends JkRepoFile {

    public Test1() {
        super(TestModel.DB_FOLDER, "test1", "trymodel.entities1");
    }

    @Test
    public void testRepo1_A() {
        Test1 test1 = new Test1();

        Set<SimpleTypes> dataSet = test1.getDataSet(SimpleTypes.class);

        SimpleTypes st = new SimpleTypes("simtyp");
        st.setNums(true, false, 1, 11, 2L, 22L, 3f, 33f, 4d, 44d);
        st.setOthers(LocalTime.NOON, LocalDate.now(), LocalDateTime.now(), new File("pfile.txt"), Paths.get("ppath.csv"));

        dataSet.add(st);
        dataSet.add(st);
        display("\n1");
        dataSet.forEach(ds -> display("{}", ds));

        SimpleTypes st2 = new SimpleTypes("secondo");
        st2.setNums(false, false, 1, 11, 2L, 22L, 3f, 33f, 4d, 44d);
        st2.setOthers(LocalTime.NOON, LocalDate.now(), LocalDateTime.now(), new File("pfile.txt"), Paths.get("ppath.csv"));

        dataSet.add(st2);
        display("\n2");
        dataSet.forEach(ds -> display("{}", ds));

        SimpleTypes st3 = new SimpleTypes("terzo");
        st3.setNums(false, false, 1, 11, 2L, 22L, 3f, 33f, 4d, 44d);
        st3.setOthers(LocalTime.NOON, LocalDate.now(), LocalDateTime.now(), new File("pfile.txt"), Paths.get("ppath.csv"));

        SimpleTypes st4 = new SimpleTypes("nullo");

        dataSet.add(st3);
        dataSet.add(st4);
        display("\n3");
        dataSet.forEach(ds -> display("{}", ds));

        test1.commit();
    }

    @Test
    public void testRepo1_B() {
        Test1 test1 = new Test1();
        Set<SimpleTypes> dataSet = test1.getDataSet(SimpleTypes.class);
        dataSet.forEach(ds -> display("{}", ds));
    }


}