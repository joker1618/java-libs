package junit.entity.correct.depsDesign;

import xxx.joker.libs.repo.design.SimpleRepoEntity;
import xxx.joker.libs.repo.design.annotation.marker.EntityPK;

public class ClassRoom extends SimpleRepoEntity {

    @EntityPK
    private String roomLabel;

    public ClassRoom() {
    }

    public ClassRoom(String roomLabel) {
        this.roomLabel = roomLabel;
    }

    public String getRoomLabel() {
        return roomLabel;
    }

    public void setRoomLabel(String roomLabel) {
        this.roomLabel = roomLabel;
    }

}
