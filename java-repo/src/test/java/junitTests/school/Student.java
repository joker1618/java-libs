package junitTests.school;

import xxx.joker.libs.core.test.JkDataTest;
import xxx.joker.libs.repo.design.SimpleRepoEntity;
import xxx.joker.libs.repo.design.annotation.marker.EntityPK;

public class Student extends SimpleRepoEntity {

    @EntityPK
    private String name;

    public Student() {
    }

    public Student(String name) {
        this.name = name;
    }
    public Student(JkDataTest dt) {
        this.name = dt.nextName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
