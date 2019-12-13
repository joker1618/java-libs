package tests;

import org.junit.Before;
import org.junit.Test;
import xxx.joker.libs.core.file.JkFiles;
import xxx.joker.libs.core.test.JkDataTest;
import xxx.joker.libs.repo.JkRepo;
import xxx.joker.libs.repo.design.SimpleRepoEntity;
import xxx.joker.libs.repo.design.annotation.directive.CascadeDelete;
import xxx.joker.libs.repo.design.annotation.directive.NoPrimaryKey;
import xxx.joker.libs.repo.design.annotation.marker.EntityField;
import xxx.joker.libs.repo.design.annotation.marker.EntityPK;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static xxx.joker.libs.core.util.JkConsole.display;
import static xxx.joker.libs.core.util.JkConvert.toList;

public class TestCascadeDelete {

    final Path repoFolder = Paths.get("C:\\Users\\fede\\IdeaProjects\\LIB\\java-libs-branch-refactor-repo\\java-repo\\src\\test\\resources\\repoTests\\cascadeDelete");
    JkRepo repo;
    JkDataTest dt;

    @Before
    public void before() {
        JkFiles.delete(repoFolder);
        repo = JkRepo.builder()
                .setRepoFolder(repoFolder)
                .setDbName("fid")
                .addClasses(Section.class, Employee.class, Recap.class)
                .buildRepo();
        dt = new JkDataTest(15);
    }

    @Test
    public void testCascadeDelete() {

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

        display("AFTER");
        display(repo.toStringRepo());
    }

    public Recap createRecap(String name, List<Section> sections, List<Employee> employees) {
        Recap recap = repo.getOrAdd(new Recap(name));
        recap.mainSection = sections.get(0);
        recap.chief = employees.get(0);
        recap.allSections.addAll(sections);
        recap.allEmployee.addAll(employees);
        return recap;
    }

    private static class Employee extends SimpleRepoEntity {
        @EntityPK
        String name;

        public Employee() {
        }

        public Employee(String name) {
            this.name = name;
        }
    }
    private static class Section extends SimpleRepoEntity {
        @EntityPK
        String name;

        public Section() {
        }

        public Section(String name) {
            this.name = name;
        }
    }

    @NoPrimaryKey
    private static class Recap extends SimpleRepoEntity {

        @EntityField
        String name;
        @EntityField
        @CascadeDelete
        Section mainSection;
        @EntityField
        @CascadeDelete
        Employee chief;
        @EntityField
        @CascadeDelete
        private List<Section> allSections;
        @EntityField
        @CascadeDelete
        private List<Employee> allEmployee;
        
        
        public Recap() {
        }


        public Recap(String name) {
            this.name = name;
        }
    }

}

