package junitTests.cascadeDelete;

import xxx.joker.libs.repo.design.SimpleRepoEntity;
import xxx.joker.libs.repo.design.annotation.marker.EntityPK;

public class Section extends SimpleRepoEntity {

    @EntityPK
    private String name;

    public Section() {
    }

    public Section(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
