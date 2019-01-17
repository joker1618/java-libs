package trymodel;

import org.junit.Test;
import xxx.joker.libs.repository.JkDataRepo;
import xxx.joker.libs.repository.JkDataRepoFile;

import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class Test1 extends JkDataRepoFile implements JkDataRepo {

    protected Test1() {
        super(TestModel.DB_FOLDER, "test1", "trymodel.entities1");
    }

    @Test
    public void test1() {
        Test1 test1 = new Test1();



    }
}
