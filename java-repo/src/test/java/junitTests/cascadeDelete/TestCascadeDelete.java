package junitTests.cascadeDelete;

import org.junit.Before;
import org.junit.Test;
import xxx.joker.libs.core.file.JkFiles;
import xxx.joker.libs.core.test.JkDataTest;
import xxx.joker.libs.repo.JkRepo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static xxx.joker.libs.core.util.JkConsole.display;
import static xxx.joker.libs.core.util.JkConvert.toList;

public class TestCascadeDelete {

    Path repoFolder;
    JkRepo repo;
    JkDataTest dt;

    @Before
    public void before() {
        repoFolder = Paths.get("src/test/resources/repos/cascadeDelete").toAbsolutePath();
//        display(repoFolder);
        JkFiles.delete(repoFolder);
        repo = JkRepo.builder()
                .setRepoFolder(repoFolder)
                .setDbName("fid")
                .addClasses(Section.class, Employee.class, Recap.class)
                .buildRepo();
        dt = new JkDataTest(15);
    }

    @Test
    public void testCascadeDeleteInMemory() {

        List<Section> sections1 = toList(new Section("A1"), new Section("A2"));
        List<Employee> employees1 = dt.nextElements(() -> new Employee("a_" + dt.nextName()), 3);
        Recap recap1 = createRecap("SUN", sections1, employees1);

        List<Section> sections2 = toList(new Section("B1"), new Section("B2"));
        List<Employee> employees2 = dt.nextElements(() -> new Employee("b_" + dt.nextName()), 3);
        Recap recap2 = createRecap("CLOUD", sections2, employees2);

        display("BEFORE");
        display(repo.toStringRepo());

        display(repo.remove(recap2));
        display(repo.removeAll(employees1));
//        employees1.forEach(e -> repo.removeFromDependencies(e, recap1));
        repo.updateDependencies(recap1);

        display("AFTER");
        display(repo.toStringRepo());
    }

    @Test
    public void testCascadeDeleteCommitAndReadFromFiles() {

        List<Section> sections1 = toList(new Section("A1"), new Section("A2"));
        List<Employee> employees1 = dt.nextElements(() -> new Employee("a_" + dt.nextName()), 3);
        Recap recap1 = createRecap("SUN", sections1, employees1);

        List<Section> sections2 = toList(new Section("B1"), new Section("B2"));
        List<Employee> employees2 = dt.nextElements(() -> new Employee("b_" + dt.nextName()), 3);
        Recap recap2 = createRecap("CLOUD", sections2, employees2);

        display("A1\n{}", repo.toStringRepo());
        repo.commit();
        repo.rollback();
        display("A2\n{}", repo.toStringRepo());

        display(repo.remove(recap2));
        display(repo.removeAll(employees1));
        repo.updateDependencies(recap1);

        display("AFTER\n{}", repo.toStringRepo());
    }


    public Recap createRecap(String name, List<Section> sections, List<Employee> employees) {
        Recap recap = repo.getOrAdd(new Recap(name));
        recap.allSections.addAll(sections);
        recap.allEmployee.addAll(employees);
        recap.mainSection = sections.get(0);
        recap.chief = employees.get(0);
        return recap;
    }

}

