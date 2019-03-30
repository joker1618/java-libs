package trymodel;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import trymodel.entities2.CustomAndCollections;
import trymodel.entities2.CustomEntityJk;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.repository.JkRepoFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class Test2 extends JkRepoFile  {

    public Test2() {
        super(TestModel.DB_FOLDER, "test2", "trymodel.entities2");
    }

    @Test
    public void testRepo2_A() {
        Test2 test2 = new Test2();

        CustomEntityJk ce1 = new CustomEntityJk();
        ce1.setPair(Pair.of("chiave", "val"));
        CustomEntityJk ce2 = new CustomEntityJk();
        ce2.setPair(Pair.of("we", "we"));

        List<Integer> intlist = Arrays.asList(1, 3, 5);
        Set<Path> pathset = JkConvert.toHashSet(Arrays.asList(Paths.get("path1"), Paths.get("path2")));
        List<CustomEntityJk> celist = Arrays.asList(ce1, ce2);

        CustomAndCollections cac1 = new CustomAndCollections("fede", ce1, intlist, pathset, celist);
        CustomAndCollections cac2 = new CustomAndCollections();
        cac2.setKeyword("pippo");

        Set<CustomAndCollections> dataSet = test2.getDataSet(CustomAndCollections.class);

        dataSet.addAll(Arrays.asList(cac1, cac2));
        dataSet.forEach(ds -> display("{}", ds));

        test2.commit();
    }

    @Test
    public void testRepo1_B() {
        Test2 test2 = new Test2();
        Set<CustomAndCollections> dataSet = test2.getDataSet(CustomAndCollections.class);
        dataSet.forEach(ds -> display("{}", ds));
    }


}