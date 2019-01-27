package xxx.joker.apps.agenda.model.entities;

import xxx.joker.libs.repository.design.JkEntityField;
import xxx.joker.libs.repository.design.JkRepoEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class Event extends JkRepoEntity {

    @JkEntityField(idx = 0)
    private LocalDateTime datetime;
    @JkEntityField(idx = 1)
    private String title;
    @JkEntityField(idx = 2, collectionType = String.class)
    private Set<String> tags;
    @JkEntityField(idx = 3)
    private String notes;
    @JkEntityField(idx = 4, collectionType = FileAttach.class)
    private List<FileAttach> attachList;


    public static Comparator<Event> temporalComparator() {
        return (e1, e2) -> {
            int res = e1.getDatetime().compareTo(e2.getDatetime());
            if(res != 0)    return res;

            res = e1.getTitle().compareTo(e2.getTitle());
            if(res != 0)    return res;

            return (int)(e1.getEntityID() - e2.getEntityID());
        };
    }

    @Override
    public String getPrimaryKey() {
        return getEntityID()+"";
    }

    public LocalDate getDate() {
        return datetime.toLocalDate();
    }

    public LocalTime getTime() {
        return datetime.toLocalTime();
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Collection<String> tags) {
        this.tags = new TreeSet<>(tags);
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
