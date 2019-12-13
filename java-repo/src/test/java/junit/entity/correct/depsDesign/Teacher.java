package junit.entity.correct.depsDesign;

import xxx.joker.libs.core.test.JkDataTest;
import xxx.joker.libs.repo.design.SimpleRepoEntity;
import xxx.joker.libs.repo.design.annotation.marker.EntityPK;

public class Teacher extends SimpleRepoEntity {

    @EntityPK
    private String name;

    public Teacher() {
    }

    public Teacher(JkDataTest dt) {
        this.name = dt.nextName();
    }
    public Teacher(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
