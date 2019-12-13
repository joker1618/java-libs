package tests;

import org.junit.Test;
import xxx.joker.libs.core.test.JkDataTest;
import xxx.joker.libs.repo.JkRepo;
import xxx.joker.libs.repo.design.SimpleRepoEntity;
import xxx.joker.libs.repo.design.annotation.marker.EntityPK;
import xxx.joker.libs.repo.design.annotation.marker.ForeignID;

import java.nio.file.Paths;

import static xxx.joker.libs.core.util.JkConsole.display;
import static xxx.joker.libs.core.util.JkConsole.displayColl;

public class TestForeignID {

    @Test
    public void testForeignID() {
        JkRepo repo = JkRepo.builder()
                .setRepoFolder(Paths.get("C:\\Users\\fede\\IdeaProjects\\LIB\\java-libs-branch-refactor-repo\\java-repo\\src\\test\\resources\\repoTests\\foreignId"))
                .setDbName("fid")
                .addClasses(Driver.class, Team.class)
                .buildRepo();

        JkDataTest dt = new JkDataTest(15);

        Team t1 = repo.getOrAdd(new Team("ferrari"));
        repo.add(new Driver("kimi", t1.getEntityId()));
        repo.add(new Driver("leclerc", t1.getEntityId()));
        Team t2 = repo.getOrAdd(new Team("red bull"));
        repo.add(new Driver("verstappen", t2.getEntityId()));
        repo.add(new Driver("albon", t2.getEntityId()));
//        Team t3 = repo.getOrAdd(new Team("mclaren"));
//        repo.add(new Driver("sainz", t3.getEntityId()));
//        repo.add(new Driver("norris", t3.getEntityId()));

        display("BEFORE");
        display(repo.toStringRepo());

        boolean removed = repo.remove(t2);
        display("Removed {}: {}", t2, removed);

        display("AFTER");
        display(repo.toStringRepo());
    }

    private static class Driver extends SimpleRepoEntity {

        @EntityPK
        String name;
        @ForeignID
        Long teamID;

        public Driver() {
        }

        public Driver(String name, Long teamID) {
            this.name = name;
            this.teamID = teamID;
        }
    }

    private static class Team extends SimpleRepoEntity {

        @EntityPK
        String name;

        public Team() {
        }

        public Team(String name) {
            this.name = name;
        }
    }

}

