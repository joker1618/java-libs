package xxx.joker.apps.agenda.model.entities;

import xxx.joker.libs.repository.design.JkEntityField;
import xxx.joker.libs.repository.design.JkRepoEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public class Event extends JkRepoEntity {

    @JkEntityField(idx = 0)
    private LocalDate date;
    @JkEntityField(idx = 1)
    private LocalTime time;
    @JkEntityField(idx = 2, collectionType = String.class)
    private Set<String> tags;
    @JkEntityField(idx = 3)
    private String title;
    @JkEntityField(idx = 4)
    private String notes;
    @JkEntityField(idx = 5, collectionType = FileAttach.class)
    private List<FileAttach> attachList;


    @Override
    public String getPrimaryKey() {
        return getEntityID()+"";
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<FileAttach> getAttachList() {
        return attachList;
    }

    public void setAttachList(List<FileAttach> attachList) {
        this.attachList = attachList;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
