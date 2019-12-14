package junitTests.cascadeDelete;

import xxx.joker.libs.repo.design.SimpleRepoEntity;
import xxx.joker.libs.repo.design.annotation.directive.CascadeDelete;
import xxx.joker.libs.repo.design.annotation.directive.NoPrimaryKey;
import xxx.joker.libs.repo.design.annotation.marker.EntityField;

import java.util.List;
import java.util.Set;

@NoPrimaryKey
public class Recap extends SimpleRepoEntity {

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
    List<Section> allSections;
    @EntityField
    @CascadeDelete
    Set<Employee> allEmployee;


    public Recap() {
    }


    public Recap(String name) {
        this.name = name;
    }
}
