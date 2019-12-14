package junitTests.school;

import xxx.joker.libs.core.test.JkDataTest;
import xxx.joker.libs.repo.design.SimpleRepoEntity;
import xxx.joker.libs.repo.design.annotation.marker.EntityField;
import xxx.joker.libs.repo.design.annotation.directive.NoPrimaryKey;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static xxx.joker.libs.core.lambda.JkStreams.*;
import static xxx.joker.libs.core.util.JkConvert.toList;
import static xxx.joker.libs.core.util.JkConvert.toTreeSet;

@NoPrimaryKey
public class LessonsPlan extends SimpleRepoEntity {

    @EntityField
    private ClassRoom mainRoom;
    @EntityField
    private Set<Teacher> allTeachers;
    @EntityField
    private List<Student> allStudents;
    @EntityField
    private Map<Teacher, String> teacherLabel;
    @EntityField
    private Map<Teacher, Set<String>> teacherLabels;
    @EntityField
    private Map<Teacher, Student> mentorMap;
    @EntityField
    private Map<Teacher, List<Student>> teacherStudents;
    @EntityField
    private Map<String, Student> studentNames;
    @EntityField
    private Map<Boolean, List<Student>> studentGroups;

    public LessonsPlan() {
    }

    public LessonsPlan(JkDataTest dt) {
        this.mainRoom = new ClassRoom("1A");
        this.allTeachers = toTreeSet(dt.nextElements(() -> new Teacher(dt), 2));
        this.allStudents = dt.nextElements(() -> new Student(dt), 5);
        this.teacherLabel = toMapSingle(allTeachers, Function.identity(), Teacher::getName);
        this.teacherLabels = toMapSingle(allTeachers, Function.identity(), t -> {
            int middle = t.getName().length() / 2;
            return toTreeSet(t.getName().substring(0, middle), t.getName().substring(middle));
        });
        this.mentorMap = toMapSingle(allTeachers, Function.identity(), t -> allStudents.get(dt.nextInt(allStudents.size())));
        int stud4teacher = allStudents.size() / allTeachers.size();
        AtomicInteger counter = new AtomicInteger(0);
        this.teacherStudents = new TreeMap<>();
        allTeachers.forEach(t -> {
            List<Student> students = allStudents.subList(counter.get() * stud4teacher, counter.incrementAndGet() * stud4teacher);
            this.teacherStudents.put(t, students);
        });
        this.studentNames = toMapSingle(allStudents, Student::getName);
        String thresold = sorted(allStudents).get(allStudents.size() / 2).getName();
        this.studentGroups = toMap(allStudents, s -> s.getName().charAt(0) <= thresold.charAt(0));
    }


}
